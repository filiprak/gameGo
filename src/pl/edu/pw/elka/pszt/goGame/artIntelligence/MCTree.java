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
		Queue<MCNode> nodes = new LinkedList<MCNode>();
		nodes.add(root);
		int numMoves = 4;
		
		// init tree with 2 levels of all available moves
		for (int i = 0; i < 5; ++i) {

			MCNode parent;
			if (nodes.isEmpty())
				parent = root;
			else
				parent = nodes.poll();
			// go up through tree
			for (int j = 0; j < numMoves; ++j) {
				
				// add new node
				Random rand = new Random();
				MCNode newNode = new MCNode(parent, parent.getBoard());
				newNode.number = rand.nextInt(1000);
				parent.addChild(newNode);

				// simulate
				int result = simulate(newNode.getBoard());
				nodes.add(newNode);

				MCNode current = newNode;

				// propagate upwards
				while (current != null) {
					current.wonGames += result;
					current.lostGames += 1 - result;
					current = current.parent;
				}
			}
		}

		// standard monte carlo tree build
		for (int i = 0; i < 10; ++i) {
			MCNode node = root;
			// go up through tree
			while (!node.children.isEmpty()) {
				float maxRatio = -1;
				for (MCNode child : node.children) {
					float rat = (float) child.wonGames / (child.wonGames + child.lostGames);
					System.out.print(" " + rat);
					if (rat > maxRatio) {
						node = child;
						maxRatio = rat;
					}
				}
				System.out.println();
			}
			
			// add new node
			Random rand = new Random();
			MCNode newNode = new MCNode(node, node.getBoard());
			newNode.number = rand.nextInt(1000);
			node.addChild(newNode);

			// simulate
			int result = simulate(newNode.getBoard());

			MCNode current = newNode;

			// propagate upwards
			while (current != null) {
				current.wonGames += result;
				current.lostGames += 1 - result;
				current = current.parent;
			}
		}
		System.out.println("\n-----------------------------------------------");
		System.out.println(this.toString());

		return 0;
	}

	/**
	 * @param board
	 * @return
	 */
	private int simulate(Board board) {
		Random rand = new Random();
		return rand.nextInt(2);
	}

	@Override
	public String toString() {
		string = new String("");
		offset = new String("");

		walkTree(root);
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
