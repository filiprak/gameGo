package pl.edu.pw.elka.pszt.goGame.model;

import java.util.List;
public class Model {
	
	Board board; //board of a game
	public static final int WHITEPLAYER = 0, BLACKPLAYER = 1;
	
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
		countPoints (currentBoard);
		if ((currentBoard.getBlackPoints()) < (currentBoard.getWhitePoints()))
			return WHITEPLAYER;
		else if((currentBoard.getBlackPoints()) > (currentBoard.getWhitePoints()))
			return BLACKPLAYER;

		return 0;
	}
	
	/**
	 * count points and get a winner 
	 * @return
	 */
	public int getWinner() {
		
		return getWinner( board );
		
	}
	
	public int countOnes(int mask)
	{
		int nr = 0;
		for (int i = 0; i<25; ++i)
		{
			if(((mask >> i) & 1) == 1)
				nr += 1;
		}
		return nr;
	}
	
	public void countPoints (Board currentBoard)
	{
		int cross;
		
		
		for (int y = 0; y < Board.BOARDSIZE; ++y) {
			for (int x = 0; x < Board.BOARDSIZE; ++x) {
				cross = Board.BOARDSIZE * y + x;
				if (((currentBoard.getWhiteStones() >> cross) & 1) == 1) //white stone on cross
					currentBoard.setWhitePoints(currentBoard.getWhitePoints() + 1);
				else if (((currentBoard.getBlackStones() >> cross) & 1) == 1) //black stone on cross
					currentBoard.setBlackPoints(currentBoard.getBlackPoints() + 1);

				else if ((((currentBoard.getWhiteNonstones() >> cross) & 1) == 1) && (((currentBoard.getBlackNonstones() >> cross) & 1) == 0)) { //empty cross and invalid for whites
					if((Board.getBreath(cross, currentBoard.getBlackStones())) > 0)	//got black neighbours
						currentBoard.setBlackPoints(currentBoard.getBlackPoints() + 1);
				}
				else if ((((currentBoard.getWhiteNonstones() >> cross) & 1) == 0) && (((currentBoard.getBlackNonstones() >> cross) & 1) == 1)) { //empty cross and invalid for blacks
					if((Board.getBreath(cross, currentBoard.getWhiteStones())) > 0) //got white neighbours
						currentBoard.setWhitePoints(currentBoard.getWhitePoints() + 1);
				}
				//empty cross invalid for both or empty cross valid for both
				else if((countOnes(Board.getBreath(cross, currentBoard.getBlackStones()))) > (countOnes(Board.getBreath(cross, currentBoard.getWhiteStones())))) //got more black neighbours
								currentBoard.setBlackPoints(currentBoard.getBlackPoints() + 1);
				else if((countOnes(Board.getBreath(cross, currentBoard.getBlackStones()))) < (countOnes(Board.getBreath(cross, currentBoard.getWhiteStones())))) // got more white neighbours
								currentBoard.setWhitePoints(currentBoard.getWhitePoints() + 1);
				
					
			}
		
		}
	}
	
	public int getPoints(int player, Board currentBoard) {
		countPoints(currentBoard);
		if (player == WHITEPLAYER)
			return currentBoard.getWhitePoints();
		else if (player == BLACKPLAYER)
			return currentBoard.getBlackPoints();
		
		

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
