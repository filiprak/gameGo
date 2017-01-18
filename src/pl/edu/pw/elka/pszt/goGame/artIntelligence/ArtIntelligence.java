package pl.edu.pw.elka.pszt.goGame.artIntelligence;

import pl.edu.pw.elka.pszt.goGame.model.Board;
import pl.edu.pw.elka.pszt.goGame.view.AIOptions;


public class ArtIntelligence {
	
	private char stonesColor;
	private MCTree tree;
	
	public ArtIntelligence(char stonesColor, Board board) {
		this.stonesColor = stonesColor;
		tree = new MCTree(board, stonesColor);
	}
	
	public int makeMove() {
		int res = tree.makeMove();
		System.out.println(tree.toShortString());
		return res;
	}
	
	public void moveRoot(int position) {
		tree.moveRoot(position);
	}
	
	public void setOptions(AIOptions options) {
		tree.setOptions(options);
	}
	
	public AIOptions getOptions() {
		return tree.getOptions();
	}
}
