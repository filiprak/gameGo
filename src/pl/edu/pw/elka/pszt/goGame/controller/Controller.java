package pl.edu.pw.elka.pszt.goGame.controller;

<<<<<<< 8a21157e21a003bd8e5c2d9cd33146445a505e81
import pl.edu.pw.elka.pszt.goGame.artIntelligence.ArtIntelligence;

import pl.edu.pw.elka.pszt.goGame.model.Board;
=======
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

>>>>>>> View and Controller upgraded
import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.View;

public class Controller {
	//private final Model model;
	//private final View view;

	char currentTurn;
	
	public Controller() {
<<<<<<< 8a21157e21a003bd8e5c2d9cd33146445a505e81
		model = new Model(Board.BLACKSGN);
		currentTurn = Board.BLACKSGN;
=======
		model = new Model();
>>>>>>> View and Controller upgraded

	}
	
	public int getWhiteStones() {
		return model.getWhiteStones();
	}
	
	public int getBlackStones() {
		return model.getBlackStones();
	}
	
	public void makeMove(int position) {
		ArtIntelligence AI = new ArtIntelligence(Board.WHITESGN);
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
	
	public int getWhitePoints() {
		return model.getWhitePoints();
	}
	public int getBlackPoints() {
		return model.getBlackPoints();
	}
	
	public void newGame() {
		model = new Model();
		view.updatePanel();
		
	}
	
	Model model;
	View view;
	
}
