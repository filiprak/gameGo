package pl.edu.pw.elka.pszt.goGame.model;

import java.util.List;

public class Model {
	
	
	/**
	 * @return
	 */
	public List<Integer> getValidMoves() {
		return null;
		
	}
	
	/**
	 * @param board
	 * @return
	 */
	public List<Integer> getValidMoves(final Board board) {
		return null;
	}
	
	/**
	 * @return
	 */
	public int getWinner() {
		return 0;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public int getPoints(int player) {
		return 0;
	}
	
	/**
	 * @return
	 */
	public boolean isEnded() {
		return false;
	}
	
	/**
	 * @param board
	 * @return
	 */
	public boolean isEnded(final Board board) {
		return false;
	}
	
	/**
	 * @param board
	 * @return
	 */
	public Board makeMove(Board board) {
		return null;
	}
}
