package pl.edu.pw.elka.pszt.goGame.artIntelligence;

import java.util.ArrayList;

import pl.edu.pw.elka.pszt.goGame.model.Board;


public class MCNode {
	private Board board;
	public int wonGames, lostGames;
	
	public ArrayList<MCNode> children;
	public MCNode parent;
	
	public int number;
	public int moveNum;
	
	public MCNode(MCNode parent, Board board) {
		wonGames = 0;
		lostGames = 0;
		children = new ArrayList<MCNode>();
		this.parent = parent;
		this.board = board;
	}

	public void addChild(MCNode child) {
		children.add(child);
	}
	
	public Board getBoard() {
		return board;
	}


	public void setBoard(Board board) {
		this.board = board;
	}
	
}
