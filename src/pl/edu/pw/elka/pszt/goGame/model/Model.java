package pl.edu.pw.elka.pszt.goGame.model;

import java.util.List;
public class Model {
	
	Board board; //board of a game
	public static final int WHITEPLAYER = 0, BLACKPLAYER = 1; //int's for players
	private char currentTurn;
	private static double KOI = 2.5;
	
	public Model(char firstPlayer) {
		board = new Board(firstPlayer);
		setValidMoves(board);
	}
	
	/**
	 * get valid moves from any board
	 * @param board
	 * @return
	 */
	public static int setValidMoves(Board currentBoard) {
		if( currentBoard.isEnded() ) {
			currentBoard.setValidMoves(0);
			return 0;
		}
		int validMoves = currentBoard.getCurrentPlayerFreeCrosses();
		int deleteMoves = 0;
		int allStones = currentBoard.getAllStones();
		for (int i = 0; i < Board.getCrosses(); ++i) {
			if( (validMoves & ( 1 << i )) == 0 ) continue;	//only check empty crosses	
			//if( Board.getBreath( i, allStones ) != 0 ) continue;	//stone has at least one breath, can be placed there
			currentBoard.putStone( i );
			int deletedStones = getDeletedStones( currentBoard );
			if( ( deletedStones & currentBoard.getCurrentPlayerStones() ) != 0 ) {
				validMoves &= (~( 1 << i ) & Board.BOARDMASK);							//putting stone there will delete some of players stones
			} 
			else if( ( deletedStones & currentBoard.getCurrentOpponentStones()) != 0 ) {
					deleteMoves |= ( 1 << i );
			}
			currentBoard.deletePlayerStone( i );
		}
		currentBoard.setDeleteMoves(deleteMoves);
		currentBoard.setValidMoves(validMoves);
		return validMoves;
	}
	
	public static int getWinner( Board currentBoard ) {
		if ((currentBoard.getBlackPoints()) <= (currentBoard.getWhitePoints()))
			return WHITEPLAYER;
		else // if((currentBoard.getBlackPoints()) > (currentBoard.getWhitePoints()))
			return BLACKPLAYER;
	}
	
	/**
	 * count points and get a winner 
	 * @return
	 */
	public int getWinner() {
		return getWinner( board );
	}
	
	public static int countOnes(int mask)
	{
		int nr = 0;
		for (int i = 0; i<25; ++i)
		{
			if(((mask >> i) & 1) == 1)
				nr += 1;
		}
		return nr;
	}
	
	public static void countPoints (Board currentBoard)
	{
		int cross;
		currentBoard.setWhitePoints(currentBoard.getWhitePoints() + KOI);
		for (int y = 0; y < Board.BOARDSIZE; ++y) {
			for (int x = 0; x < Board.BOARDSIZE; ++x) {
				cross = Board.BOARDSIZE * y + x;
				if (((currentBoard.getWhiteStones() >> cross) & 1) == 1) //white stone on cross
					currentBoard.setWhitePoints(currentBoard.getWhitePoints() + 1);
				else if (((currentBoard.getBlackStones() >> cross) & 1) == 1) //black stone on cross
					currentBoard.setBlackPoints(currentBoard.getBlackPoints() + 1);

				else if ((((currentBoard.getWhiteNonstones() >> cross) & 1) == 1) && (((currentBoard.getBlackNonstones() >> cross) & 1) == 0)) { //empty cross and invalid for whites
					if((Board.getNeighbour(cross, currentBoard.getBlackStones())) > 0)	//got black neighbours
						currentBoard.setBlackPoints(currentBoard.getBlackPoints() + 1);
				}
				else if ((((currentBoard.getWhiteNonstones() >> cross) & 1) == 0) && (((currentBoard.getBlackNonstones() >> cross) & 1) == 1)) { //empty cross and invalid for blacks
					if((Board.getNeighbour(cross, currentBoard.getWhiteStones())) > 0) //got white neighbours
						currentBoard.setWhitePoints(currentBoard.getWhitePoints() + 1);
				}
				//empty cross invalid for both or empty cross valid for both
				else if((countOnes(Board.getNeighbour(cross, currentBoard.getBlackStones()))) > (countOnes(Board.getNeighbour(cross, currentBoard.getWhiteStones())))) //got more black neighbours
								currentBoard.setBlackPoints(currentBoard.getBlackPoints() + 1);
				else if((countOnes(Board.getNeighbour(cross, currentBoard.getBlackStones()))) < (countOnes(Board.getNeighbour(cross, currentBoard.getWhiteStones())))) // got more white neighbours
								currentBoard.setWhitePoints(currentBoard.getWhitePoints() + 1);
			}
		}
	}
	
	public void countPoints () {
		countPoints(board);
	}
	
	
	public double getPoints(int player, Board currentBoard) {
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
	public double getPoints(int player) {
		return getPoints(player, board);
	}
	
	public boolean isEnded(Board currentBoard) {
		return currentBoard.isEnded();
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
	public static char makeMove(Board currentBoard, int position) {
		if( position >= Board.getCrosses() ) {
			currentBoard.resignCurrentPlayer();
		}
		else {
			currentBoard.putStone( position );
			int deletedStones = getDeletedStones( currentBoard );
			currentBoard.deleteStones( deletedStones );
		}
		char currentPlayer = currentBoard.switchTurn();
		setValidMoves(currentBoard);
		return currentPlayer;
	}
	
	/**
	 * make move on models board
	 * @param position
	 * @return 
	 */
	public char makeMove(int position) {
		System.out.println("move: " + position + " ");
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

	public String getBoard() {
		return board.toString();
	}
	
	public Board getGameBoard() {
		return board;
	}
	
	public Board getBoardObject() {
		return board;
	}
	
	public int getValidMoves(){
		return board.getValidMoves();
	}
	
	public int getWhiteStones() {
		return board.getWhiteStones();
	}
	
	public int getBlackStones() {
		return board.getBlackStones();
	}
	public double getWhitePoints() {
		return board.getWhitePoints();
	}
	public double getBlackPoints() {
		return board.getBlackPoints();
	}
	public void setKoiPoints(double k) {
		KOI = k;
	}
	public double getKoitPoints() {
		return KOI;
	}
}
