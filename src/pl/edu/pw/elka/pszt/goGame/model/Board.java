package pl.edu.pw.elka.pszt.goGame.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public static final int BOARDSIZE = 5;
	public static final char WHITESGN = 'W', BLACKSGN = 'B', EMPTYSGN = '+';
	public static final int BOARDMASK = 0x01FFFFFF;
	public static final int DELETED_STONE_POINT = 1;
	
	private int whiteStones, blackStones, whiteNonstones, blackNonstones;
	private int whitePoints, blackPoints;
	private boolean whiteMoves, blackMoves;
	private char currentTurn;
	private int validMoves;
	
	public Board() {
		whiteStones = 0x00000000;
		blackStones = 0x00000000;
		whiteNonstones = 0x00000000;
		blackNonstones = 0x00000000;
		currentTurn = BLACKSGN;
		whiteMoves = true;
		blackMoves = true;
	}
	
	public Board(Board b) {
		whiteStones = b.whiteStones;
		blackStones = b.blackStones;
		whiteNonstones = b.whiteNonstones;
		blackNonstones = b.blackNonstones;
		currentTurn = b.currentTurn;
		whiteMoves = b.whiteMoves;
		blackMoves = b.blackMoves;
		validMoves = b.validMoves;
}
	

	public void putStone(int position) throws RuntimeException {
		if(position > BOARDSIZE * BOARDSIZE || position < 0)
			throw new RuntimeException("Trying to put a stone outside the board !");
		if(((whiteStones >> position) & 1) == 1 || ((blackStones >> position) & 1) == 1)
			throw new RuntimeException("Trying to put a stone on occupied cross !");
		if( currentTurn == WHITESGN && ((1 << position) & whiteNonstones) != 0 )
			throw new RuntimeException("Trying to put a stone on a restricted cross !");
		if( currentTurn == BLACKSGN && ((1 << position) & blackNonstones) != 0 )
			throw new RuntimeException("Trying to put a stone on a restricted cross !");
		
		if (currentTurn == WHITESGN) {
			whiteStones |= (1 << position);
		} 
		else if(currentTurn == BLACKSGN){
			blackStones |= (1 << position);
		}
	}
	
	public char switchTurn() {
		if( currentTurn == WHITESGN ) {
			currentTurn = blackMoves ? BLACKSGN : WHITESGN;
		} 
		else if( currentTurn == BLACKSGN ) {
			currentTurn = whiteMoves ? WHITESGN : BLACKSGN;
		}
		return currentTurn;
	}
	
	public void deleteStone(int position) throws RuntimeException {
		if(position > BOARDSIZE * BOARDSIZE || position < 0)
			throw new RuntimeException("Trying to delete a stone outside the board !");
		if(((whiteStones >> position) & 1) == 0 && ((blackStones >> position) & 1) == 0 )
			throw new RuntimeException("Trying to delete a stone from empty cross !");
		
		if (currentTurn == BLACKSGN) {
			whiteStones &= ( ~(1 << position) & BOARDMASK);
			whiteNonstones |= ( (1 << position) & BOARDMASK);
		} else if(currentTurn == WHITESGN){
			blackStones &= (~(1 << position) & BOARDMASK);
			blackNonstones |= ((1 << position) & BOARDMASK);
		}
		increaseCurrentPlayersPoints(DELETED_STONE_POINT);
	}
	
	public void deletePlayerStone(int position) throws RuntimeException {
		if(position > BOARDSIZE * BOARDSIZE || position < 0)
			throw new RuntimeException("Trying to delete a stone outside the board !");
		if(((whiteStones >> position) & 1) == 0 && ((blackStones >> position) & 1) == 0 )
			throw new RuntimeException("Trying to delete a stone from empty cross !");
		
		if (currentTurn == WHITESGN) {
			whiteStones &= ( ~(1 << position) & BOARDMASK);
		} else if(currentTurn == BLACKSGN){
			blackStones &= (~(1 << position) & BOARDMASK);
		}
	}
	
	public void deleteStones( int stones ) throws RuntimeException {
		for( int i = 0; i < BOARDSIZE * BOARDSIZE; ++i)
		{
			if( (stones & ( 1 << i)) != 0 ) {
				deleteStone( i ) ;
			}
		}
	}
	
	public void clear() {
		whiteStones = 0x00000000;
		blackStones = 0x00000000;
		whiteNonstones = 0x00000000;
		blackNonstones = 0x00000000;
	}
	
	static public int getBreath( int position, int allStones ) {
		int breathMask = 0x000008A2; //010001010001000...
		int leftSideMask = 0x00108421; //1000010000100001000010000...
		int rightSideMask = 0x01084210; //0000100001000010000100001...
		int bottomSideMask = 0x01F00000; //000000000000000000001111100...
		int topSideMask = 0x0000001F; //111110000...
		
		if( (( 1 << position ) & leftSideMask) != 0 ) {
			breathMask &= ((~0x00000020) & BOARDMASK);
		}
		if( (( 1 << position ) & rightSideMask) != 0 ) {
			breathMask &= ((~0x00000080) & BOARDMASK);
		}
		if( (( 1 << position ) & bottomSideMask) != 0 ) {
			breathMask &= ((~0x00000800) & BOARDMASK);
		}
		if( (( 1 << position ) & topSideMask) != 0 ) {
			breathMask &= ((~00000002) & BOARDMASK);
		}
		
		if( position < 6) 
			breathMask = ( breathMask >> (6 - position) );
		else
			breathMask = ( breathMask << (position - 6) );
		return ((~allStones & BOARDMASK) & breathMask);
	}
	
	static public int getNeighbour( int position, int allStones ) {
		int breathMask = 0x000008A2; //010001010001000...
		int leftSideMask = 0x00108421; //1000010000100001000010000...
		int rightSideMask = 0x01084210; //0000100001000010000100001...
		int bottomSideMask = 0x01F00000; //000000000000000000001111100...
		int topSideMask = 0x0000001F; //111110000...
		
		if( (( 1 << position ) & leftSideMask) != 0 ) {
			breathMask &= ((~0x00000020) & BOARDMASK);
		}
		if( (( 1 << position ) & rightSideMask) != 0 ) {
			breathMask &= ((~0x00000080) & BOARDMASK);
		}
		if( (( 1 << position ) & bottomSideMask) != 0 ) {
			breathMask &= ((~0x00000800) & BOARDMASK);
		}
		if( (( 1 << position ) & topSideMask) != 0 ) {
			breathMask &= ((~00000002) & BOARDMASK);
		}
		
		if( position < 6) 
			breathMask = ( breathMask >> (6 - position) );
		else
			breathMask = ( breathMask << (position - 6) );
		return ((allStones & BOARDMASK) & breathMask);
	}
	
	
	public int getBoardsize() {
		return BOARDSIZE;
	}
	
	public int getAllStones() {
		return blackStones | whiteStones;
	}
	
	public int getCurrentPlayerStones() {
		return currentTurn == WHITESGN ? whiteStones : blackStones;
	}
	
	public int getCurrentOpponentStones() {
		return currentTurn == BLACKSGN ? whiteStones : blackStones;
	}
	
	public int getCurrentPlayerNonstones() {
		return currentTurn == WHITESGN ? whiteNonstones : blackNonstones;
	}
	
	public int getCurrentOpponent() {
		return currentTurn == BLACKSGN ? whiteNonstones : blackNonstones;
	}
	
	public int getCurrentPlayerFreeCrosses() {
		return currentTurn == WHITESGN ? (~(getAllStones() | whiteNonstones)) & BOARDMASK  : (~(getAllStones() | blackNonstones)) & BOARDMASK;
	}
	
	public void increaseCurrentPlayersPoints( int points ) {
		if( currentTurn == WHITESGN ) {
			whitePoints += points;
		} 
		else if( currentTurn == BLACKSGN ) {
			blackPoints += points;
		}
	}
	
	public static int getCrosses() {
		return BOARDSIZE * BOARDSIZE;
	}
	
	public int getValidMoves() {
		return validMoves;
	}
	
	public void setValidMoves( int vM ) {
		validMoves = vM;
		validMoves |= (1 << getCrosses());
	}
	
	public void resignCurrentPlayer() {
		if( currentTurn == WHITESGN ) whiteMoves = false;
		if( currentTurn == BLACKSGN ) blackMoves = false;
	}
	
	public boolean isEnded() {
		return (!whiteMoves && !blackMoves); //both players pass'ed
	}
	
	//@Override
	public String toString() {
		String string = new String("");
		int cross;
		for (int y = 0; y < BOARDSIZE; ++y) {
			for (int x = 0; x < BOARDSIZE; ++x) {
				cross = BOARDSIZE * y + x;
				if (((whiteStones >> cross) & 1) == 1)
					string += WHITESGN + " ";
				else if (((blackStones >> cross) & 1) == 1)
					string += BLACKSGN + " ";
				else
					string += EMPTYSGN + " ";
			}
			string += "\n";
		}
		return string;
	}

	public int getWhiteStones() {
		return whiteStones;
	}

	public void setWhiteStones(int whiteStones) {
		this.whiteStones = whiteStones;
	}

	public int getWhitePoints() {
		return whitePoints;
	}

	public void setWhitePoints(int whitePoints) {
		this.whitePoints = whitePoints;
	}

	public int getBlackStones() {
		return blackStones;
	}

	public void setBlackStones(int blackStones) {
		this.blackStones = blackStones;
	}

	public int getBlackPoints() {
		return blackPoints;
	}

	public void setBlackPoints(int blackPoints) {
		this.blackPoints = blackPoints;
	}

	public int getWhiteNonstones() {
		return whiteNonstones;
	}

	public void setWhiteNonstones(int whiteNonstones) {
		this.whiteNonstones = whiteNonstones;
	}
	
	public int getBlackNonstones() {
		return blackNonstones;
	}

	public void setBlackNonstones(int blackNonstones) {
		this.blackNonstones = blackNonstones;
	}
}
