package pl.edu.pw.elka.pszt.goGame;

import java.util.Scanner;

import pl.edu.pw.elka.pszt.goGame.artIntelligence.ArtIntelligence;
import pl.edu.pw.elka.pszt.goGame.artIntelligence.MCTree;
import pl.edu.pw.elka.pszt.goGame.model.Board;
import pl.edu.pw.elka.pszt.goGame.model.Model;

public class Main {

	public static void main(String[] args) {
		Model model = new Model(Board.BLACKSGN);
		ArtIntelligence engine =  new ArtIntelligence(Board.WHITESGN);
		Scanner reader = new Scanner(System.in);

		while(!model.isEnded()) {
			System.out.println(model.getBoard());
			System.out.println("Enter a number: ");
			int n = reader.nextInt(); // Scans the next token of the input as an int.
			if( ((1 << n) & model.getValidMoves()) != 0) {
				System.out.println();
				if (model.getBoardObject().isBlackMoves())
					model.makeMove(n);
				System.out.println(model.getBoard());
				System.out.println(MCTree.binary(model.getValidMoves()));
				if (model.getBoardObject().isWhiteMoves())
					model.makeMove(engine.makeMove(model.getBoardObject()));
			}
			else {
				System.out.println("tried to make invalid move!");
			}
		}
		model.countPoints();
		System.out.print("White player: ");
		System.out.println(model.getPoints(0));
		System.out.print("Black player: ");
		System.out.println(model.getPoints(1));
	}
}
