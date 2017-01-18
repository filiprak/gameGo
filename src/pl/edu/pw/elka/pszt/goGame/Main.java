package pl.edu.pw.elka.pszt.goGame;

import java.io.IOException;
import java.util.Scanner;

import pl.edu.pw.elka.pszt.goGame.artIntelligence.ArtIntelligence;
import pl.edu.pw.elka.pszt.goGame.artIntelligence.MCTree;
import pl.edu.pw.elka.pszt.goGame.model.Board;
import pl.edu.pw.elka.pszt.goGame.model.Model;
import pl.edu.pw.elka.pszt.goGame.view.View;

public class Main {

	public static void main(String[] args) throws IOException {
		View view = new View();
		view.createWindow();
	}
}
