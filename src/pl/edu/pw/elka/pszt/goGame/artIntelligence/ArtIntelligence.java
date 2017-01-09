package pl.edu.pw.elka.pszt.goGame.artIntelligence;

import pl.edu.pw.elka.pszt.goGame.model.Board;

public class ArtIntelligence {
	
	private char stonesColor;
	
	public ArtIntelligence(char stonesColor) {
		this.stonesColor = stonesColor;
	}
	
	public int makeMove(Board board) {
		MCTree tree = new MCTree(board, stonesColor);
		int res = tree.makeMove();
		System.out.println(tree.toShortString());
		return res;
	}
}
