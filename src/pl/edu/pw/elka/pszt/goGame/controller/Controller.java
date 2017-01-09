package pl.edu.pw.elka.pszt.goGame.controller;

<<<<<<< 9a3ee1b1fbaa08c14be7800b1b87f424a081e9af
=======
import pl.edu.pw.elka.pszt.goGame.artIntelligence.ArtIntelligence;
>>>>>>> PLaying with computer
import pl.edu.pw.elka.pszt.goGame.model.Board;
import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.View;

public class Controller {
	//private final Model model;
	//private final View view;

	char currentTurn;
	
	public Controller() {
		model = new Model(Board.BLACKSGN);
<<<<<<< 9a3ee1b1fbaa08c14be7800b1b87f424a081e9af
		model.makeMove(1);
		model.makeMove(3);
		model.makeMove(6);
=======
		currentTurn = Board.BLACKSGN;
>>>>>>> PLaying with computer
	}
	
	public int getWhiteStones() {
		return model.getWhiteStones();
	}
	
	public int getBlackStones() {
		return model.getBlackStones();
	}
	
	public void makeMove(int position) {
		ArtIntelligence AI = new ArtIntelligence();
		int validMoves = model.getValidMoves();
		if( ((1 << position) & validMoves) != 0) {
			currentTurn = model.makeMove(position);
			while( currentTurn == Board.WHITESGN ) {
				currentTurn = model.makeMove(AI.makeMove(model.getGameBoard()));
			}
		}
		else {
			//incorrect move
		}
	}
	
	Model model;
	View view;
	
}
