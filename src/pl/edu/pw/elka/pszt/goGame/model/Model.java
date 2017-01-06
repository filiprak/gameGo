package pl.edu.pw.elka.pszt.goGame.model;

import java.util.List;

public class Model {
	
	
	/**
	 * get valid moves from any board
	 * @param board
	 * @return
	 */
	int getValidMoves(Board currentBoard) {
		int validMoves = 0;		
		for (int i = 0; i < currentBoard.getBoardsize() * currentBoard.getBoardsize(); ++i) {
			
		}
		return validMoves;
	}
	
	/**
	 * get valid moves from current board in model
	 * @return
	 */
	public int getValidMoves() {
		return 0;
	}
	
	/**
	 * count points and get a winner 
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
	public Board makeMove(Board board, int position) {
		return null;
	}
	
	/**
	 * make move on models board
	 * @param position
	 * @return 
	 */
	public boolean makeMove(int position) {
		return false;
	}
	
	private int getDeletedStones(Board board) {
		return 0;
	}
}
