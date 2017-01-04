package pl.edu.pw.elka.pszt.goGame.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
	public static final int BOARDSIZE = 5;
	public static final char WHITESGN = 'W', BLACKSGN = 'B', EMPTYSGN = '+';
	
	private int whiteStones, blackStones;
	
	public Board() {
		whiteStones = 0x00000000;
		blackStones = 0x00000000;
	}
	
	public void putStone(int position, char color) throws RuntimeException {
		if(position > BOARDSIZE * BOARDSIZE || position < 1)
			throw new RuntimeException("Trying to put a stone outside the board !");
		if(((whiteStones >> position) & 1) == 1 || ((blackStones >> position) & 1) == 1)
			throw new RuntimeException("Trying to put a stone on occupied cross !");
		
		if (color == WHITESGN) {
			whiteStones |= (1 << position);
		} else if(color == BLACKSGN){
			blackStones |= (1 << position);
		} else
			return;
	}
	
	public void clear() {
		whiteStones = 0x00000000;
		blackStones = 0x00000000;
	}
	
	public List<Integer> getValidMoves(char color) {
		List<Integer> validMoves = new ArrayList<Integer>();
		int allStones = whiteStones | blackStones;
		
		for (int i = 1; i < BOARDSIZE * BOARDSIZE + 1; ++i) {
			if(((allStones >> i) & 1) == 1) // skip all occupied positions
				continue;
			// todo reszta przypadkow
			validMoves.add(i);
		}
		return validMoves;
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
