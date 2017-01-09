package pl.edu.pw.elka.pszt.goGame.artIntelligence;

import pl.edu.pw.elka.pszt.goGame.model.Board;

public class ArtIntelligence {
	
	public int makeMove(Board board) {
		MCTree tree = new MCTree(board);
		int res = tree.makeMove();
		System.out.println(tree.toShortString());
		return res;
	}
}
