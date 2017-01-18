package pl.edu.pw.elka.pszt.goGame.controller;


import pl.edu.pw.elka.pszt.goGame.artIntelligence.ArtIntelligence;

import pl.edu.pw.elka.pszt.goGame.model.Board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;


import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.AIOptions;
import pl.edu.pw.elka.pszt.goGame.view.View;

public class Controller {
	//private final Model model;
	//private final View view;

	char currentTurn;
	private ArtIntelligence AI;
	private char opponentsColor;
	private char newGameColor;
	
	
	public Controller(View vview) {
		model = new Model(Board.BLACKSGN);
		currentTurn = Board.BLACKSGN;
		view = vview;
		//model = new Model();
		opponentsColor = Board.BLACKSGN;
		newGameColor = Board.WHITESGN;
		AI = new ArtIntelligence(opponentsColor, model.getBoardObject());
		if( opponentsColor == Board.BLACKSGN) {
			int move = AI.makeMove();
			currentTurn = model.makeMove(move);
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
		if( model.isEnded() ) {
			return;
		}
		int validMoves = model.getValidMoves();
		if( currentTurn == opponentsColor) return;
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
		if( model.isEnded() ) {
			view.showResults();
			return;
		}
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
	
	public AIOptions getOptions() {
		AIOptions options = AI.getOptions();
		options.newGameColor = this.newGameColor;
		options.koi_points = model.getKoitPoints();
		return options;
	}
	
	public void setOptions(AIOptions options) {
		newGameColor = options.newGameColor;
		model.setKoiPoints(options.koi_points);
		AI.setOptions(options);
	}
	
	
	public double getWhitePoints() {
		return model.getWhitePoints();
	}
	public double getBlackPoints() {
		return model.getBlackPoints();
	}
	
	public void newGame() {
		AIOptions options = AI.getOptions();
		model = new Model(Board.BLACKSGN);
		currentTurn = Board.BLACKSGN;
		opponentsColor = newGameColor == Board.WHITESGN ? Board.BLACKSGN : Board.WHITESGN;
		AI = new ArtIntelligence(opponentsColor, model.getBoardObject());
		AI.setOptions(options);
		if( opponentsColor == Board.BLACKSGN) {
			int move = AI.makeMove();
			currentTurn = model.makeMove(move);
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
