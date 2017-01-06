package pl.edu.pw.elka.pszt.goGame.artIntelligence;

import java.util.Random;

import pl.edu.pw.elka.pszt.goGame.model.Board;

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
		for (int i = 0; i < 10; ++i) {
			// go up through tree
			MCNode node = root;
			while (!node.children.isEmpty()) {
				int maxVisits = -1;
				for (MCNode child : node.children) {
					if (child.wonGames > maxVisits) {
						node = child;
						maxVisits = child.wonGames;
					}
				}
			}
			
			// add new node
			MCNode newNode = new MCNode(node, node.getBoard());
			newNode.number = i+100;
			node.addChild(newNode);
			// simulate
			int result = simulate(newNode.getBoard());
			
			// propagate upwards
			while(node != null) {
				node.wonGames += result;
				node.lostGames += 1 - result;
				node = node.parent;
			}
		}
		
		return 0;
	}

	/**
	 * @param board
	 * @return
	 */
	private int simulate(Board board) {
		Random rand = new Random();
		return rand.nextInt(3) % 2; 
	}
	
	@Override
	public String toString() {
		string = new String("");
		offset = new String("");
		
		walkTree(root);
		return string;
	}
	
	private void walkTree(MCNode node) {
		String nodeStr = new String(node.number + "#(" +
				node.wonGames + "/" + (node.wonGames + node.lostGames) + ")\n");
		
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
