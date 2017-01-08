package pl.edu.pw.elka.pszt.goGame.artIntelligence;

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

	public MCTree(Board rootBoard) {
		root = new MCNode(null, rootBoard);
	}

	public MCNode getRoot() {
		return root;
	}

	/**
	 * @param detailLevel
	 * @return
	 */
	public String getInfo(int detailLevel) {
		return null;

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
		for (int i = 0; i < 10000; ++i) {
			MCNode node = root; // go up through tree
			while (!node.children.isEmpty()) {
				float maxRatio = -1;
				for (MCNode child : node.children) {
					float rat = (float) child.wonGames / (child.wonGames + child.lostGames);
					if (rat > maxRatio) {
						node = child;
						maxRatio = rat;
					}
				}
			}

			// add new node
			ArrayList<Integer> moves = new ArrayList<Integer>();
			int validMoves = node.getBoard().getValidMoves();
			int numValidMoves = 0;
			for (int j = 0; j < 26; ++j) {
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
			Model.makeMove(childBoard, moves.get(randomMoveId));
			MCNode newNode = new MCNode(node, node.getBoard());
			newNode.number = rand.nextInt(1000);
			node.addChild(newNode);

			Board simulBoard = new Board(newNode.getBoard());
			// simulate
			int result = simulate(simulBoard);

			MCNode current = newNode;

			// propagate upwards
			while (current != null) {
				current.wonGames += result;
				current.lostGames += 1 - result;
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
		
		System.out.println(node.getBoard().toString());
		System.out.println(binary(root.getBoard().getAllStones() ^ node.getBoard().getAllStones()));
		
		return root.getBoard().getAllStones() ^ node.getBoard().getAllStones();
	}

	public static int getRandom(int[] array) {
		int rnd = new Random().nextInt(array.length);
		return array[rnd];
	}
	
	private int addAllValidMoves(MCNode parent) {
		Model.setValidMoves(parent.getBoard());
		int validMoves = parent.getBoard().getValidMoves();
		//System.out.println(binary(validMoves));
		int numValidMoves = 0;

		// go up through tree
		for (int j = 0; j < 26; ++j) {

			if ((validMoves & (1 << j)) == 0) // skip invalid moves
				continue;
			numValidMoves++;
			// add new node
			Random rand = new Random();
			Board childBoard = new Board(parent.getBoard());
			Model.makeMove(childBoard, j);
			// System.out.println(childBoard.toString());

			MCNode newNode = new MCNode(parent, childBoard);
			newNode.number = rand.nextInt(1000);
			parent.addChild(newNode);

			Board simulBoard = new Board(newNode.getBoard());
			// simulate
			int result = simulate(simulBoard);

			MCNode current = newNode;

			// propagate upwards
			while (current != null) {
				current.wonGames += result;
				current.lostGames += 1 - result;
				current = current.parent;
			}
		}
		return numValidMoves;
	}

	private String binary(int number) {
		String string = new String("");
		for(int i = 0; i < 32; ++i) {
			if (i == 25) string += " ";
			if ((number & (1 << i)) == 0)
				string += 0;
			else
				string += 1;
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
			for (int j = 0; j < 26; ++j) {
				if ((validMoves & (1 << j)) == 0) // skip invalid moves
					continue;
				numValidMoves++;
				moves.add(j);
			}
			if (numValidMoves == 0)
				break;
			int randomMoveId = new Random().nextInt(numValidMoves);
			Model.makeMove(board, moves.get(randomMoveId));
		}
		
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
			string += "| " + child.number + "#(" + child.wonGames + "/" +
					(child.wonGames + child.lostGames) + ")\n";
		}
		
		return string;
	}

	private void walkTree(MCNode node) {
		String nodeStr = new String(
				node.number + "#(" + node.wonGames + "/" + (node.wonGames + node.lostGames) + ")\n");

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
