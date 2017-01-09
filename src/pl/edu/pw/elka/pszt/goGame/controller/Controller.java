package pl.edu.pw.elka.pszt.goGame.controller;

import pl.edu.pw.elka.pszt.goGame.model.Board;
import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.View;

public class Controller {
	//private final Model model;
	//private final View view;

	public Controller() {
		model = new Model(Board.BLACKSGN);
		model.makeMove(1);
		model.makeMove(3);
		model.makeMove(6);
	}
	
	public int getWhiteStones() {
		return model.getWhiteStones();
	}
	
	public int getBlackStones() {
		return model.getBlackStones();
	}
	
	public void makeMove(int position) {
		model.makeMove(position);
	}
	
	Model model;
	View view;
	
}
