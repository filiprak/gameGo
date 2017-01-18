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
	
	private MCNode root;
	private String string, offset;
	public static int num = 0;
	public static int movesMaskSize = 26;
	private char stonesColor;
	
	public MCTree(Board rootBoard, char stonesColor) {
		root = new MCNode(null, rootBoard, 0);
		num = 0;
		this.stonesColor = stonesColor;
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
	 * @return
	 */
	public int makeMove() {
		// init tree with 2 levels of all available moves
		/*addAllValidMoves(root);
		for (MCNode child : root.children) {
			addAllValidMoves(child);
		}*/
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
			if( node == null ) {
				break;
			}
			
			int randomedPosition = 25;
			
			int deleteMoves = node.getBoard().getDeleteMoves();
			if( deleteMoves != 0 ) {
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
						for( int k = 0; k < 3; ++k ) {	
							numValidMoves++;
							moves.add(j);
						}
					}
					else {
						numValidMoves++;
						moves.add(j);
					}	
				}
				int randomMoveId = rand.nextInt(numValidMoves);
				randomedPosition = moves.get(randomMoveId);
			}
			
			
			
			//System.out.print(moves.get(randomMoveId) + "\n");
			
			Board childBoard = new Board(node.getBoard());
			Model.makeMove(childBoard, randomedPosition);
			MCNode newNode = new MCNode(node, childBoard, node.getLevel()+1);
			newNode.number = ++num;
			newNode.moveNum = randomedPosition;
			node.addChild(newNode);
			
			node.deleteSimulationMove(randomedPosition);
			if( newNode.getBoard().isEnded() ) {
				newNode.end();
			}
			if( newNode.getBoard().resignedPlayer() ) {
				newNode.end();
			}

			for( int k = 0; k < SIMULATIONS_PER_NODE; ++k)
			{
				Board simulBoard = new Board(newNode.getBoard());
				// simulate
				int result = simulate(simulBoard);
			
				MCNode current = newNode;
			
				// propagate upwards
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
			
			if( newNode.wonGames > 0 && randomedPosition == 25 && node.getBoard().getCurrentTurn() == stonesColor ) {
				if( node == root ) {
					return 25;
				}
				node.end();
			}
		}
		
		int deleteMoves = root.getBoard().getDeleteMoves();
		if( deleteMoves != 0 ) {
			for (int j = 0; j < movesMaskSize; ++j) {
				if ((deleteMoves & (1 << j)) != 0) { // skip invalid moves
					return j;
				}
			}
		}

		// return best turn
		float maxSimuls = -1;
		MCNode node = root;
		for (MCNode child : root.children) {
			float simuls = (float)( (float)child.wonGames / (float)(child.wonGames + child.lostGames));
			if (simuls > maxSimuls) {
				node = child;
				maxSimuls = simuls;
			}
		}
		if( CHILDREN_LIMIT_JUMP > 3 ) --CHILDREN_LIMIT_JUMP;

		return node.moveNum;
	}
	
	private int addAllValidMoves(MCNode parent) {
		int validMoves = parent.getBoard().getValidMoves();
		//System.out.println(binary(validMoves));
		int numValidMoves = 0;

		// go up through tree
		for (int j = 0; j < movesMaskSize; ++j) {

			if ((validMoves & (1 << j)) == 0) // skip invalid moves
				continue;
			numValidMoves++;
			// add new node
			Board childBoard = new Board(parent.getBoard());
			Model.makeMove(childBoard, j);
			// System.out.println(childBoard.toString());

			MCNode newNode = new MCNode(parent, childBoard, root.getLevel() + 1);
			newNode.number = ++num;
			newNode.moveNum = j;
			parent.addChild(newNode);

			Board simulBoard = new Board(newNode.getBoard());
			// simulate
			int result = simulate(simulBoard);
			
			MCNode current = newNode;

			// propagate upwards
			while (current != null) {
				if (stonesColor == Board.WHITESGN) {
					current.wonGames += 1 - result;
					current.lostGames += result;
				} else {
					current.wonGames += result;
					current.lostGames += 1 - result;
				}
				current = current.parent;
			}
		}
		return numValidMoves;
	}

	private MCNode maxRatioChild() {
		MCNode node = root; // go up through tree
		double limit = ( CHILDREN_LIMIT / CHILDREN_LIMIT_JUMP );
		while (!node.children.isEmpty()) {
			MCNode bestChild = node; // go up through tree
			float maxRatio;
			if( node == root ) {
				maxRatio = node.choosingChildren() ? -1 : 3;
			} else {
				maxRatio = node.choosingChildren() ? -1 : calculateRatio(node.parent, node) + PARENT_POINTS;	//don't choose worse children unless you have to
			}
			bestChild = node;
			for (MCNode child : node.children) {
				if( child.isEnded() ) {
					continue;
				}
				if( child.wonGames + child.lostGames > limit  ) {
					continue;
				}
				if( child.getLevel() - root.getLevel() > TREE_DEPTH ) {
					continue;
				}
				float rat = calculateRatio(node, child);
				if (rat > maxRatio) {
					bestChild = child;
					maxRatio = rat;
				}
			}
			if( bestChild == node ) {
				//there was no children to choose from so add new child to higher node
				if( node.choosingChildren() ) {
					if( node == root ) {	//root has no valid children, nothing to do!
						return null;
					}
					node.end();
					node = node.parent;
					limit = limit * CHILDREN_LIMIT_JUMP;
				}
				else {
					return node;
				}
			}
			else {
				node = bestChild;
				limit = limit / CHILDREN_LIMIT_JUMP;
			}
		}
		return node;
	}
	
	private float calculateRatio(MCNode parent, MCNode child) {
		float ratio = (float) child.wonGames / (child.wonGames + child.lostGames);
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
	 * @param board
	 * @return
	 */
	private int simulate(Board board) {
		if( board.resignedPlayer() ) {
			int validMoves = board.getValidMoves();
			for (int j = 0; j < movesMaskSize; ++j) {
				if ((validMoves & (1 << j)) == 0) // skip invalid moves
					continue;
				Model.makeMove(board, j);
			}
		}
		while (!board.isEnded()) {
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
				ArrayList<Integer> moves = new ArrayList<Integer>();
				int validMoves = board.getValidMoves();
				int numValidMoves = 0;
				for (int j = 0; j < movesMaskSize; ++j) {
					if ((validMoves & (1 << j)) == 0) // skip invalid moves
						continue;
					numValidMoves++;
					moves.add(j);
				}
				if (numValidMoves == 0)
					break;
				int randomMoveId = new Random().nextInt(numValidMoves);
				/*if (moves.get(randomMoveId) == 25)
				return stonesColor == Board.BLACKSGN ? */
				Model.makeMove(board, moves.get(randomMoveId));
			}
		}
		
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
	
	public void moveRoot(int position) {
		for( MCNode child : root.children ) 
		{
			if( child.moveNum == position ) {
				root = child;
				root.parent = null;
				return;
			}
		}
		Board gameBoard = new Board(root.getBoard());
		Model.makeMove(gameBoard, position);
		int level = root.getLevel();
		root = new MCNode(null, gameBoard, level+1);
	}
}
