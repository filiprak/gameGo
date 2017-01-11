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
	
	public Controller(View vview) {

		model = new Model(Board.BLACKSGN);
		currentTurn = Board.BLACKSGN;
		view = vview;
		//model = new Model();


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
		ArtIntelligence AI = new ArtIntelligence(Board.WHITESGN);
		int validMoves = model.getValidMoves();
		if( ((1 << position) & validMoves) != 0) {
			currentTurn = model.makeMove(position);
			if(model.isEnded()) {
				view.showResults();
				return;
			}
			while( currentTurn == Board.WHITESGN ) {
				currentTurn = model.makeMove(AI.makeMove(model.getGameBoard()));
				if(model.isEnded()) {
					view.showResults();
					return;
				}
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
		model = new Model(Board.BLACKSGN);
		view.updatePanel();
	}
	public void exitGame() {
		view.getFrame().dispatchEvent(new WindowEvent(view.getFrame(), WindowEvent.WINDOW_CLOSING));
	}
	
	Model model;
	View view;
	
}
