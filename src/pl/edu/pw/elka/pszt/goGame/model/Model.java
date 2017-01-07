package pl.edu.pw.elka.pszt.goGame.model;

import java.util.List;
public class Model {
	
	Board board; //board of a game
	
	/**
	 * get valid moves from any board
	 * @param board
	 * @return
	 */
	public static int getValidMoves(Board currentBoard) {
		int validMoves = currentBoard.getCurrentPlayerFreeCrosses();
		int allStones = currentBoard.getAllStones();
		for (int i = 0; i < Board.getCrosses(); ++i) {
			if( (validMoves & ( 1 << i )) == 0 ) continue;	//only check empty crosses
			//System.out.println(Integer.toString(Board.getBreath( i, allStones ), 2));			
			if( Board.getBreath( i, allStones ) != 0 ) continue;	//stone has at least one breath, can be placed there
			currentBoard.putStone( i );
			if( ( getDeletedStones( currentBoard ) & currentBoard.getCurrentPlayerStones() ) != 0 ) 
				validMoves &= (~( 1 << i ) & Board.BOARDMASK);							//putting stone there will delete some of players stones
			currentBoard.deletePlayerStone( i );
		}
		return validMoves;
	}
	
	/**
	 * get valid moves from current board in model
	 * @return
	 */
	public int getValidMoves() {
		return getValidMoves( board );
	}
	
	public int getWinner( Board currentBoard ) {
		return 0;
	}
	
	/**
	 * count points and get a winner 
	 * @return
	 */
	public int getWinner() {
		return getWinner( board );
	}
	
	public int getPoints(int player, Board currentBoard) {
		return 0;
	}
	
	/**
	 * @param player
	 * @return
	 */
	public int getPoints(int player) {
		return getPoints(player, board);
	}
	
	public boolean isEnded(Board currentBoard) {
		return false;
	}
	
	/**
	 * @return
	 */
	public boolean isEnded() {
		return isEnded( board );
	}
	
	/**
	 * @param board
	 * @return
	 */
	public boolean makeMove(Board currentBoard, int position) {
		board.putStone( position );
		int deletedStones = getDeletedStones( currentBoard );
		currentBoard.deleteStones( deletedStones );
		return true;
	}
	
	/**
	 * make move on models board
	 * @param position
	 * @return 
	 */
	public boolean makeMove(int position) {
		return makeMove( board, position );
	}
	
	private static int getDeletedStones( int playerStones, int allStones ) {
		boolean deleted = true;
		while( deleted ) {
			deleted = false;
			for( int i = 0; i < Board.getCrosses(); ++i) {
				if( (playerStones & ( 1 << i )) == 0 ) continue;
				if( Board.getBreath( i, allStones ) != 0) {
					allStones &= (~(1 << i) & Board.BOARDMASK);
					playerStones &= (~(1 << i) & Board.BOARDMASK);
					deleted = true;
				}
			}
		}
		return allStones & playerStones;
	}
	
	private static int getDeletedStones(Board currentBoard) {
		int deletedOpponentStones = 0;
		int deletedPlayerStones = 0;
		int allStones = currentBoard.getAllStones();
		int opponentStones = currentBoard.getCurrentOpponentStones();
		int playerStones = currentBoard.getCurrentPlayerStones();
		
		//first delete from all stones all deleted stones of opponent
		deletedOpponentStones = getDeletedStones( opponentStones, allStones);
		allStones &= (~deletedOpponentStones & Board.BOARDMASK);
		//then check if after that some players stones aren't to be destroyed
		deletedPlayerStones = getDeletedStones( playerStones, allStones );
		
		return (deletedPlayerStones | deletedOpponentStones);
	}
}
