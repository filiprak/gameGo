package pl.edu.pw.elka.pszt.goGame.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public static final int BOARDSIZE = 5;
	public static final char WHITESGN = 'W', BLACKSGN = 'B', EMPTYSGN = '+';
	public static final int DELETE_POINT = 1;
	
	private int whiteStones, blackStones, whiteNonstones, blackNonstones;
	private int whitePoints, blackPoints;
	private boolean whiteMoves, blackMoves;
	private char currentTurn;
	
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
	}
	
	public void putStone(int position, char color) throws RuntimeException {
		if(position > BOARDSIZE * BOARDSIZE || position < 1)
			throw new RuntimeException("Trying to put a stone outside the board !");
		if(((whiteStones >> position) & 1) == 1 || ((blackStones >> position) & 1) == 1)
			throw new RuntimeException("Trying to put a stone on occupied cross !");
		if( currentTurn == WHITESGN && ((1 << position) & whiteNonstones) != 0 )
			throw new RuntimeException("Trying to put a stone on a restricted cross !");
		if( currentTurn == BLACKSGN && ((1 << position) & blackNonstones) != 0 )
			throw new RuntimeException("Trying to put a stone on a restricted cross !");
		
		if (color == WHITESGN) {
			whiteStones |= (1 << position);
		} 
		else if(color == BLACKSGN){
			blackStones |= (1 << position);
		}
		
		if( currentTurn == WHITESGN ) {
			currentTurn = blackMoves ? BLACKSGN : WHITESGN;
		} 
		else if( currentTurn == BLACKSGN ) {
			currentTurn = whiteMoves ? WHITESGN : BLACKSGN;
		}
	}
	
	public void deleteStone(int position) throws RuntimeException {
		if(position > BOARDSIZE * BOARDSIZE || position < 1)
			throw new RuntimeException("Trying to delete a stone outside the board !");
		if(((whiteStones >> position) & 1) == 0 && ((blackStones >> position) & 1) == 0 )
			throw new RuntimeException("Trying to delete a stone from empty cross !");
		
		if (currentTurn == WHITESGN) {
			whiteStones &= ~(1 << position);
			whiteNonstones |= (1 << position);
			blackPoints += DELETE_POINT;
		} else if(currentTurn == BLACKSGN){
			blackStones &= ~(1 << position);
			blackNonstones |= (1 << position);
			whitePoints += DELETE_POINT;
		} else
			return;
	}
	
	public void clear() {
		whiteStones = 0x00000000;
		blackStones = 0x00000000;
		whiteNonstones = 0x00000000;
		blackNonstones = 0x00000000;
	}
	
	int getBreath( int position, int allStones ) {
		int breathMask = 0x000010A2; //0100010100001000...
		int leftSideMask = 0x00108421; //1000010000100001000010000...
		int rightSideMask = 0x01084210; //0000100001000010000100001...
		int bottomSideMask = 0x01F00000; //000000000000000000001111100...
		int topSideMask = 0x0000001F; //111110000...
		
		if( (( 1 << position ) & leftSideMask) == 1 ) {
			breathMask &= ~leftSideMask;
		}
		if( (( 1 << position ) & rightSideMask) == 1 ) {
			breathMask &= ~rightSideMask;
		}
		if( (( 1 << position ) & bottomSideMask) == 1 ) {
			breathMask &= ~bottomSideMask;
		}
		if( (( 1 << position ) & topSideMask) == 1 ) {
			breathMask &= ~topSideMask;
		}
		
		breathMask = breathMask << position;
		return (allStones & breathMask);
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
		return currentTurn == WHITESGN ? (whiteStones & ~(whiteNonstones)) : (blackStones & ~(blackNonstones));
	}
	
	@Override
	public String toString() {
		String string = new String("");
		int cross;
		for (int y = 0; y < BOARDSIZE; ++y) {
			for (int x = 0; x < BOARDSIZE; ++x) {
				cross = BOARDSIZE * y + x + 1;
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
}
