package pl.edu.pw.elka.pszt.goGame.artIntelligence;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import pl.edu.pw.elka.pszt.goGame.model.Board;
import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.AIOptions;

public class MCTree {

	private int CHILDREN_LIMIT = 50000;
	private int CHILDREN_LIMIT_JUMP = 8;
	private int SIMULATIONS = 50000;
	private double EXPLORATION_RATIO = 1.418;
	private int PARENT_POINTS = 10000000;
	private int SIMULATIONS_PER_NODE = 1;
	private int TREE_DEPTH = 5;
	private boolean DECREASING_LIMIT = false;
	private int NOPAS_CHANCE = 3;
	private boolean WIN_ONLY_RATIO = false;
	
	private MCNode root;
	private String string, offset;
	public static int num = 0;
	public static int movesMaskSize = 26;
	private char stonesColor;
	
	public MCTree(Board rootBoard, char stonesColor) {
		root = new MCNode(null, rootBoard, 0);
		num = 0;
		this.stonesColor = stonesColor;
		MCNode.simulation = 0;
	}

	public MCNode getRoot() {
		return root;
	}
	
	public void setOptions(AIOptions options) {
		SIMULATIONS = options.simulations;
		CHILDREN_LIMIT = options.children_limit;
		CHILDREN_LIMIT_JUMP = options.children_limit_jump;
		EXPLORATION_RATIO = options.exploration_ratio;
		PARENT_POINTS = options.exploreParent ? 10000000 : 0;
		SIMULATIONS_PER_NODE = options.simulationsPerNode;
		TREE_DEPTH = options.treeDepth;
		DECREASING_LIMIT = options.decreasingLimit;
		WIN_ONLY_RATIO = options.winOnlyRatio;
	}
	
	public AIOptions getOptions() {
		AIOptions options = new AIOptions();
		options.simulations = SIMULATIONS;
		options.children_limit = CHILDREN_LIMIT;
		options.children_limit_jump = CHILDREN_LIMIT_JUMP;
		options.exploration_ratio = EXPLORATION_RATIO;
		options.exploreParent = PARENT_POINTS > 0;
		options.simulationsPerNode = SIMULATIONS_PER_NODE;
		options.treeDepth = TREE_DEPTH;
		options.decreasingLimit = DECREASING_LIMIT;
		options.winOnlyRatio = WIN_ONLY_RATIO;
		return options;
	}

	/**
	 * @param detailLevel
	 * @return
	 */
	public String getInfo(int detailLevel) {
		return this.toShortString();
	}

	/**
	 * create MCTS tree and find best node which will decide where to move
	 * @return
	 */
	public int makeMove() {
		//next simulation, unlock blocked nodes
		MCNode.simulation++;
		
		//create simulation for root
		Random rand = new Random();
		{
			Board simulBoard = new Board(root.getBoard());
			// simulate
			int result = simulate(simulBoard);
			
			if (stonesColor == Board.WHITESGN) {
				root.wonGames += 1 - result;
				root.lostGames += result;
			} else {
				root.wonGames += result;
				root.lostGames += 1 - result;
			}
		}
		
		// standard monte carlo tree build
		for (int i = 0; i < SIMULATIONS; ++i) {
			// choose child with maximum ratio
			MCNode node = maxRatioChild();
			//no node to create new simulations, end
			if( node == null ) {
				break;
			}
			
			int randomedPosition = 25;
			int deleteMoves = node.getBoard().getDeleteMoves();
			if( deleteMoves != 0 ) {
				//node has some moves that will delete opponents nodes
				//choose them and don't create anymore children of that node
				for (int j = 0; j < movesMaskSize; ++j) {
					if ((deleteMoves & (1 << j)) != 0) { // skip invalid moves
						node.chooseChildren();
						randomedPosition = j;
						break;
					}
				}
			}
			else {
				// add new node
				ArrayList<Integer> moves = new ArrayList<Integer>();
				int validMoves = node.getBoard().getSimulationMoves();
				int numValidMoves = 0;
				for (int j = 0; j < movesMaskSize; ++j) {
					if ((validMoves & (1 << j)) == 0) // skip invalid moves
						continue;
					if( j != 25 ) {
						//normal moves have higher chance to occur than Pas
						for( int k = 0; k < NOPAS_CHANCE; ++k ) {	
							numValidMoves++;
							moves.add(j);
						}
					}
					else {
						numValidMoves++;
						moves.add(j);
					}	
				}
				//no valid moves in given node
				if( numValidMoves == 0)
					continue;
				int randomMoveId = rand.nextInt(numValidMoves);
				randomedPosition = moves.get(randomMoveId);
			}
			
			//initialize new node and update his parent
			Board childBoard = new Board(node.getBoard());
			Model.makeMove(childBoard, randomedPosition);
			MCNode newNode = new MCNode(node, childBoard, node.getLevel()+1);
			newNode.number = ++num;
			newNode.moveNum = randomedPosition;
			node.addChild(newNode);
			node.deleteSimulationMove(randomedPosition);
			
			//player resigned so there is no need to evaluate this node further
			if( newNode.getBoard().resignedPlayer() ) {
				newNode.endDefinitely();
			}
			
			//check value of a node by creating simulations from it
			for( int k = 0; k < SIMULATIONS_PER_NODE; ++k)
			{
				Board simulBoard = new Board(newNode.getBoard());
				// simulate
				int result = simulate(simulBoard);
			
				MCNode current = newNode;
			
				// propagate results upwards
				while (current != null) {
					if (stonesColor == Board.WHITESGN) {
						current.wonGames += (1 - result);
						current.lostGames += result;
					} else {
						current.wonGames += result;
						current.lostGames += (1 - result);
					}
					current = current.parent;
				}
			}
			
			//if AI resigning here gives it a win, there is no need to explore this node further
			if( newNode.wonGames > 0 && randomedPosition == 25 && node.getBoard().getCurrentTurn() == stonesColor ) {
				if( node == root ) {
					return 25;
				}
				node.endDefinitely();
			}
		}
		
		//always choose a move that deletes players stone
		int deleteMoves = root.getBoard().getDeleteMoves();
		if( deleteMoves != 0 ) {
			for (int j = 0; j < movesMaskSize; ++j) {
				if ((deleteMoves & (1 << j)) != 0) { // skip invalid moves
					return j;
				}
			}
		}

		// return best turn with highest winrate
		float maxSimuls = -1;
		MCNode node = root;
		for (MCNode child : root.children) {
			float simuls = (float)( (float)child.wonGames / (float)(child.wonGames + child.lostGames));
			if (simuls > maxSimuls) {
				node = child;
				maxSimuls = simuls;
			}
		}
		
		//if this option was chosen
		//decrease reducing of maximum number of child nodes
		if( DECREASING_LIMIT ) {
			if( CHILDREN_LIMIT_JUMP > 3 ) --CHILDREN_LIMIT_JUMP;
		}
		
		//return best move
		return node.moveNum;
	}

	private MCNode maxRatioChild() {
		MCNode node = root; // go up through tree
		double limit = ( CHILDREN_LIMIT / CHILDREN_LIMIT_JUMP );	//set starting limit 
		while (!node.children.isEmpty()) {
			MCNode bestChild = node;
			float maxRatio;
			if( node == root ) {
				//always create a child from root if possible
				maxRatio = node.choosingChildren() ? -1 : 30000;
			} else {
				//if explore parents was set, add a lot of parent points to ensure child is never chosen unless it has to be
				maxRatio = node.choosingChildren() ? -1 : calculateRatio(node.parent, node) + PARENT_POINTS;	//don't choose worse children unless you have to
			}
			bestChild = node;
			//find best child of node
			for (MCNode child : node.children) {
				if( child.isEnded() ) {		//node is blocked, ignore it
					continue;
				}
				if( child.wonGames + child.lostGames > limit  ) { 	//too many children in node, ignore it 
					continue;
				}
				if( child.getLevel() - root.getLevel() > TREE_DEPTH ) {	//tree is too deep, ignore it
					continue;
				}
				//node can be evaluated
				float rat = calculateRatio(node, child);
				if (rat > maxRatio) {
					bestChild = child;
					maxRatio = rat;
				}
			}
			if( bestChild == node ) {
				//there was no children to choose from so add new child to higher node
				if( node.choosingChildren() ) {
					//no child was chosen even though it should have been
					//block this node and go back to its parent
					
					if( node == root ) {	//root has no valid children, nothing to do!
						return null;
					}
					//if all of nodes children ended definitely (terminated) this node is also ended for all moves
					//if not it is only blocked for this move
					boolean endDefinetly = true;
					for (MCNode child : node.children) 
					{
						if( !child.endedDefinitely() ) {
							endDefinetly = false;
							break;
						}
					}
					if( endDefinetly ) {
						node.endDefinitely();
					}
					else {
						node.end();
					}
					//no child to create here, go one level higher
					node = node.parent;
					limit = limit * CHILDREN_LIMIT_JUMP;
				}
				else {
					//no child chosen, but we can create new child from this node
					return node;
				}
			}
			else {
				//child of this node chosen
				//check if another child should be chosen
				node = bestChild;
				limit = limit / CHILDREN_LIMIT_JUMP;
			}
		}
		
		//return found node
		return node;
	}
	
	private float calculateRatio(MCNode parent, MCNode child) {
		float ratio = 0;
		if( parent.getBoard().getCurrentTurn() == stonesColor || WIN_ONLY_RATIO ) {
			ratio = (float) child.wonGames / (child.wonGames + child.lostGames);
		}
		else {
			ratio = (float) child.lostGames / (child.wonGames + child.lostGames);
		}
		ratio += EXPLORATION_RATIO * Math.sqrt(Math.log(parent.wonGames + parent.lostGames) / 
				(child.wonGames + child.lostGames));
		//float ratio = (float) child.wonGames / (child.wonGames + child.lostGames);
		return ratio;
	}
	
	public static String binary(int number) {
		String string = new String("");
		for(int i = 0; i < 32; ++i) {
			if (i%5 == 0) string +="\n";
			if (i == 25) string += " ";
			if ((number & (1 << i)) == 0)
				string += "0 ";
			else
				string += "1 ";
		}
		return string;
	}
	
	/**
	 * create simulation for given board
	 * @param board
	 * @return
	 */
	private int simulate(Board board) {
		//one player resigned
		//make rest of the moves and end
		if( board.resignedPlayer() ) {
			int validMoves = board.getValidMoves();
			for (int j = 0; j < movesMaskSize; ++j) {
				if ((validMoves & (1 << j)) == 0) // skip invalid moves
					continue;
				Model.makeMove(board, j);
			}
		}
		//play until end
		while (!board.isEnded()) {
			//always delete a stone if possible
			int deleteMoves = board.getDeleteMoves();
			if( deleteMoves != 0 ) {
				for (int j = 0; j < movesMaskSize; ++j) {
					if ((deleteMoves & (1 << j)) != 0) { // skip invalid moves
						Model.makeMove(board, j);
						break;
					}
				}
			}
			else {
				//make random move
				ArrayList<Integer> moves = new ArrayList<Integer>();
				int validMoves = board.getValidMoves();
				int numValidMoves = 0;
				for (int j = 0; j < movesMaskSize; ++j) {
					if ((validMoves & (1 << j)) == 0) // skip invalid moves
						continue;
					if( j != 25 ) {
						//normal moves have higher chance to occur than Pas
						for( int k = 0; k < NOPAS_CHANCE; ++k ) {	
							numValidMoves++;
							moves.add(j);
						}
					}
					else {
						numValidMoves++;
						moves.add(j);
					}	
				}
				if (numValidMoves == 0)
					break;
				int randomMoveId = new Random().nextInt(numValidMoves);
				Model.makeMove(board, moves.get(randomMoveId));
			}
		}
		
		//count points and return winner
		Model.countPoints(board);
		return Model.getWinner(board);
	}

	@Override
	public String toString() {
		string = new String("");
		offset = new String("");

		walkTree(root);
		
		return string;
	}
	
	public String toShortString() {
		string = new String("");
		string += root.number + "#(" + root.wonGames + "/" +
				(root.wonGames + root.lostGames) + ")\n";
		for (MCNode child : root.children) {
			string += "| " + child.moveNum + "#(" + child.wonGames + "/" +
					(child.wonGames + child.lostGames) + ")\n";
		}
		
		return string;
	}

	private void walkTree(MCNode node) {
		String nodeStr = new String(
				node.moveNum + "#(" + node.wonGames + "/" + (node.wonGames + node.lostGames) + ")\n");

		if (node.children.isEmpty()) {
			string = offset + nodeStr + string;
			return;
		}
		for (MCNode child : node.children) {
			String temp = offset;
			offset += "|  ";
			walkTree(child);
			offset = temp;
		}
		string = offset + nodeStr + string;
	}
	
	/*
	 * move tree root to match model's board
	 */
	public void moveRoot(int position) {
		//find children with given move
		for( MCNode child : root.children ) 
		{
			if( child.moveNum == position ) {
				root = child;
				root.parent = null;
				return;
			}
		}
		//no child was found, creating one
		Board gameBoard = new Board(root.getBoard());
		Model.makeMove(gameBoard, position);
		int level = root.getLevel();
		root = new MCNode(null, gameBoard, level+1);
	}
}
