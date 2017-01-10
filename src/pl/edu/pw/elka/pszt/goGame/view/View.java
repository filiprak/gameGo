package pl.edu.pw.elka.pszt.goGame.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import pl.edu.pw.elka.pszt.goGame.controller.Controller;

public class View {
	JFrame frame;
	
	public static void main(String[] args) throws IOException {
		View view = new View();
		view.createWindow();
	}
	
	private void createWindow() throws IOException {
		//1. Create the frame.
		frame = new JFrame("FrameDemo");

		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setLayout(new BorderLayout());
	
		JPanel p = new JPanel(new BorderLayout());
		p.setSize(new Dimension(100,400));
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setSize(new Dimension(300,400));
		JButton b = new JButton("test");
		//p2.add(b);
		//frame.add(p, BorderLayout.EAST);
		//p.add(b);

		//mP.showResults();
		
	    
		controller = new Controller(this);
	    
	    pane = new GamePanel(controller);
		
		mP = new MenuPanel(controller, this);
		frame.add(mP, BorderLayout.EAST);


	    frame.add(pane, BorderLayout.CENTER);
	    
		
		//4. Size the frame.
		frame.setSize(new Dimension(1000, 800));
		//frame.pack();
		
        Toolkit zestaw = Toolkit.getDefaultToolkit();
		Dimension rozmiarEkranu = zestaw.getScreenSize();
		int wys = rozmiarEkranu.height;
		int szer = rozmiarEkranu.width;
		frame.setLocation(szer/4, wys/4);
		//5. Show it.
		frame.setVisible(true);
	}
	
	public void updatePanel() {
		pane.updatePanel();
	}
	public JFrame getFrame() {
		return frame;
	}
	public void showResults() {
		mP.showResults();
	}
	
	GamePanel pane;
	MenuPanel mP;
	Controller controller;
}

class GamePanel extends JPanel {
	public GamePanel(Controller c) {
		super();
		controller = c;
		playersTurn = true;
		addMouseListener(new MouseHandler());
	}
	
	public void updatePanel() {
		this.repaint();
	}
	
	 @Override
     protected void paintComponent(Graphics g) {
     	super.paintComponent(g);
     	BufferedImage whiteStone;
     	BufferedImage blackStone;
     	BufferedImage board;
		try {
			whiteStone = ImageIO.read(new File("whiteStone.png"));
			blackStone = ImageIO.read(new File("blackStone.png"));
	     	board = ImageIO.read(new File("board.png"));
	     	
			g.drawImage(board, 0, 0, null);
	     	int stones = controller.getWhiteStones();
	     	for(int i = 0; i < 25; ++i) {
	     		if( ((stones >> i) & 1) == 1 ) {
	     			int x = i%5;
	     			int y = (i - x)/5;
	     			g.drawImage(whiteStone, STARTING_POINT + x * STONES_DISTANCE, STARTING_POINT + y * STONES_DISTANCE, null);
	     		}
	     	}
	     	stones = controller.getBlackStones();
	     	for(int i = 0; i < 25; ++i) {
	     		if( ((stones >> i) & 1) == 1 ) {
	     			int x = i%5;
	     			int y = (i - x)/5;
	     			g.drawImage(blackStone, STARTING_POINT + x * STONES_DISTANCE, STARTING_POINT + y * STONES_DISTANCE, null);
	     		}
	     	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     	
     }
	 
	 private void makeMove(double x, double y) {
		 int column = getPos(x);
		 int row = getPos(y);
		 
		 if( column == -1 || row == -1) return;
		 
		 controller.makeMove(column + row * 5);
		 this.repaint();
	 }
	 
	 private int getPos(double x) {
		 if( x < STARTING_POINT) return -1;
		 int x_coordinate = (int)x;
		 x_coordinate -= STARTING_POINT;
		 
		 System.out.println(x_coordinate);
		 if( x_coordinate % STONES_DISTANCE > 80) return -1;
		 int pos = (int)(x_coordinate / STONES_DISTANCE);
		 return pos > 4 ? -1 : pos;
	 }
	 
	 
	 
	 private Controller controller;
	 private boolean playersTurn;

	 final int STARTING_POINT = 40;
	 final int STONES_DISTANCE = 160;
  	

	/**
	 * class managing mouse actions
	 * @author Michał Glinka
	 *
	 */
	private class MouseHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent event)
		{
			double x = event.getPoint().getX();
			double y = event.getPoint().getY();
			
			if( playersTurn ) makeMove(x, y);
		}
	}	
}	
class MenuPanel extends JPanel{
	public JLabel txt;
	public JButton newGame;
	public JButton pas;
	Controller c;
	View view;

	

	public MenuPanel(Controller cc, View v) {
		c = cc;
		view = v;
		new JPanel ();
		this.setPreferredSize(new Dimension(205,800));
		this.setBackground(Color.YELLOW);
		this.txt = new JLabel("                      GO");
		this.txt.setPreferredSize(new Dimension(200, 300));
		this.add(txt);
		this.newGame = new JButton("Nowa Gra");
		this.newGame.setPreferredSize(new Dimension(200,100));
		this.newGame.addActionListener(new mPActionListener());
		this.add(newGame);
		this.pas = new JButton("Pas");
		this.pas.setPreferredSize(new Dimension(200,100));
		this.pas.addActionListener(new mPActionListener());
		this.add(pas);
	}
	
	public void showResults() {
		JFrame frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(500, 400));
		frame.setLayout(new BorderLayout());
		
        Toolkit zestaw = Toolkit.getDefaultToolkit();
		Dimension rozmiarEkranu = zestaw.getScreenSize();
		int wys = rozmiarEkranu.height;
		int szer = rozmiarEkranu.width;
		frame.setLocation(szer/3, wys/3);
		c.getCountPoints();
		if(c.getBlackPoints() < c.getWhitePoints()) {
			JLabel wW = new JLabel("<html>Wygrał Biały! <br> Punkty Białego:<html>" + Integer.toString(c.getWhitePoints()) + "<html><br> Punkty Czarnego: <html>" + Integer.toString(c.getBlackPoints()));
			frame.add(wW);

			
		}
		else if(c.getBlackPoints() > c.getWhitePoints()) {
			JLabel bW = new JLabel("<html>Wygrał Czarny!<br> Punkty Białego:<html>" + Integer.toString(c.getWhitePoints()) + "<html><br> Punkty Czarnego: <html>" + Integer.toString(c.getBlackPoints()));
			frame.add(bW);

		}
		else {
			JLabel dW = new JLabel("<html>Remis! <br> Punkty Białego:<html>" + Integer.toString(c.getWhitePoints()) + "<html><br> Punkty Czarnego: <html>" + Integer.toString(c.getBlackPoints()));
			frame.add(dW);

		}
		frame.setVisible(true);
		
	}
	public void getUpdatePanel() {
		view.updatePanel();
	} 
	
	private class mPActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == newGame)
				c.newGame();
			if(e.getSource() == pas)
				c.makeMove(25);
				getUpdatePanel();
		}	
	}
	

	
}

