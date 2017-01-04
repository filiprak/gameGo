package pl.edu.pw.elka.pszt.goGame;

import java.util.Scanner;

import pl.edu.pw.elka.pszt.goGame.model.Board;

public class Main {

	public static void main(String[] args) {
		Board board = new Board();
		Scanner reader = new Scanner(System.in);
		
		while(true) {
			System.out.println("Enter a number: ");
			int n = reader.nextInt(); // Scans the next token of the input as an int.
			board.putStone(n, Board.WHITESGN);
			System.out.println();
			System.out.println();
			System.out.println(board.toString());
			System.out.print(board.getValidMoves(Board.BLACKSGN));
		}
		
	}

}
