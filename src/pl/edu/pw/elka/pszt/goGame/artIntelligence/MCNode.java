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
	public int level;
	
	private boolean ended;
	private boolean chooseChildren;
	
	public MCNode(MCNode parent, Board board, int l) {
		wonGames = 0;
		lostGames = 0;
		children = new ArrayList<MCNode>();
		this.parent = parent;
		this.board = new Board(board);
		this.ended = false;
		this.chooseChildren = false;
		this.level = l;
	}

	public void addChild(MCNode child) {
		children.add(child);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void deleteSimulationMove(int position) {
		board.deleteSimulationMove(position);
		if( (board.getSimulationMoves() & Board.BOARDMASK) == 0) {
			chooseChildren = true;	//there are no new children to be made, so we have to choose one!
		}
	}
	
	public void end() {
		ended = true;
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public boolean choosingChildren() {
		return chooseChildren;
	}
	
	public void chooseChildren() {
		chooseChildren = true;
	}

	public void setBoard(Board board) {
		this.board = board;
	}
	
	public int getLevel() {
		return level;
	}
	
}
