package pl.edu.pw.elka.pszt.goGame.controller;


import pl.edu.pw.elka.pszt.goGame.artIntelligence.ArtIntelligence;

import pl.edu.pw.elka.pszt.goGame.model.Board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;


import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.View;

public class Controller {
	//private final Model model;
	//private final View view;

	char currentTurn;
	private ArtIntelligence AI;
	private char opponentsColor;
	public Controller(View vview) {

		model = new Model(Board.BLACKSGN);
		currentTurn = Board.BLACKSGN;
		view = vview;
		//model = new Model();
		opponentsColor = Board.BLACKSGN;
		AI = new ArtIntelligence(opponentsColor, model.getBoardObject());
		if( opponentsColor == Board.BLACKSGN) {
			int move = AI.makeMove();
			model.makeMove(move);
			AI.moveRoot(move);
		}
	}
	
	public int getWhiteStones() {
		return model.getWhiteStones();
	}
	
	public int getBlackStones() {
		return model.getBlackStones();
	}
	public void getCountPoints() {
		model.countPoints();
	}
	public void makeMove(int position) {
		int validMoves = model.getValidMoves();
		if( ((1 << position) & validMoves) != 0 && !model.isEnded() ) {
			currentTurn = model.makeMove(position);
			AI.moveRoot(position);
			if(model.isEnded()) {
				view.showResults();
				return;
			}
		}
		else {
			//incorrect move
		}
	}
	
	public void makeOpponentMove() {
		if( model.isEnded() )
			return;
		while( currentTurn == opponentsColor ) {
			int move = AI.makeMove();
			currentTurn = model.makeMove(move);
			AI.moveRoot(move);
			if(model.isEnded()) {
				view.showResults();
				return;
			}
		}
	}
	
	
	
	public int getWhitePoints() {
		return model.getWhitePoints();
	}
	public int getBlackPoints() {
		return model.getBlackPoints();
	}
	
	public void newGame() {
		model = new Model(Board.BLACKSGN);
		currentTurn = Board.BLACKSGN;
		opponentsColor = Board.BLACKSGN;
		AI = new ArtIntelligence(opponentsColor, model.getBoardObject());
		if( opponentsColor == Board.BLACKSGN) {
			int move = AI.makeMove();
			model.makeMove(move);
			AI.moveRoot(move);
		}
		view.updatePanel();
	}
	public void exitGame() {
		view.getFrame().dispatchEvent(new WindowEvent(view.getFrame(), WindowEvent.WINDOW_CLOSING));
	}
	
	Model model;
	View view;
	
}
