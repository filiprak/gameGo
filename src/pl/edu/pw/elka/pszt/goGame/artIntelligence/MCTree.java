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

public class MCTree {

	private MCNode root;
	private String string, offset;
	public static int num = 0;
	public static int movesMaskSize = 26;
	private char stonesColor;
	
	public MCTree(Board rootBoard, char stonesColor) {
		root = new MCNode(null, rootBoard);
		num = 0;
		this.stonesColor = stonesColor;
	}

	public MCNode getRoot() {
		return root;
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
		addAllValidMoves(root);
		for (MCNode child : root.children) {
			addAllValidMoves(child);
		}

		// standard monte carlo tree build
		for (int i = 0; i < 30000; ++i) {
			// choose child with maximum ratio
			MCNode node = maxRatioChild();
			if( node == root ) {
				break;
			}
			// add new node
			ArrayList<Integer> moves = new ArrayList<Integer>();
			int validMoves = node.getBoard().getValidMoves();
			int numValidMoves = 0;
			for (int j = 0; j < movesMaskSize; ++j) {
				if ((validMoves & (1 << j)) == 0) // skip invalid moves
					continue;
				numValidMoves++;
				moves.add(j);
			}
			if (numValidMoves == 0)
				continue;
			
			Random rand = new Random();
			Board childBoard = new Board(node.getBoard());
			int randomMoveId = rand.nextInt(numValidMoves);
			//System.out.print(moves.get(randomMoveId) + "\n");
			
			Model.makeMove(childBoard, moves.get(randomMoveId));
			MCNode newNode = new MCNode(node, childBoard);
			newNode.number = ++num;
			node.addChild(newNode);
			
			if( newNode.getBoard().isEnded() ) {
				newNode.end();
			}

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

		// return best turn
		int maxSimuls = -1;
		MCNode node = root;
		for (MCNode child : root.children) {
			int simuls = (child.wonGames + child.lostGames);
			if (simuls > maxSimuls) {
				node = child;
				maxSimuls = simuls;
			}
		}
		
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

			MCNode newNode = new MCNode(parent, childBoard);
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
		MCNode bestChild = root; // go up through tree
		while (!node.children.isEmpty()) {
			float maxRatio = -1;
			for (MCNode child : node.children) {
				if( child.isEnded() ) {
					continue;
				}
				float rat = calculateRatio(node, child);
				if (rat > maxRatio) {
					bestChild = child;
					maxRatio = rat;
				}
			}
			if( maxRatio == -1 ) {
				if( node == root ) {	//all children of root ended their games, return root
					return root;
				}
				node.end();
				node = node.parent;
			}
			else {
				node = bestChild;
			}
		}
		return node;
	}
	
	private float calculateRatio(MCNode parent, MCNode child) {
		float ratio = (float) child.wonGames / (child.wonGames + child.lostGames);
		ratio += 1.418 * Math.sqrt(Math.log(parent.wonGames + parent.lostGames) / 
				(child.wonGames + child.lostGames));
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
		while (!board.isEnded()) {
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
}
