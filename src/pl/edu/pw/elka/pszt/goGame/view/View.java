package pl.edu.pw.elka.pszt.goGame.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


import pl.edu.pw.elka.pszt.goGame.controller.Controller;

public class View {
	
	public static void main(String[] args) throws IOException {
		View view = new View();
		view.createWindow();
	}
	
	private void createWindow() throws IOException {
		//1. Create the frame.
		JFrame frame = new JFrame("FrameDemo");

		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		JPanel p = new JPanel(new BorderLayout());
		p.setSize(new Dimension(100,400));
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setSize(new Dimension(300,400));
		JButton b = new JButton("test");
		p2.add(b);
		frame.add(p, BorderLayout.EAST);
		p.add(b);
		
		MenuPanel mP = new MenuPanel();
		frame.add(mP, BorderLayout.WEST	);

	    
		controller = new Controller();
	    
	    pane = new GamePanel(controller);


	    frame.add(pane, BorderLayout.CENTER);
	    
		
		//4. Size the frame.
		frame.setSize(new Dimension(1000, 800));
		//frame.pack();

		//5. Show it.
		frame.setVisible(true);
	}
	
	public void updatePanel() {
		pane.updatePanel();
	}
	
	GamePanel pane;
	
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
			whiteStone = ImageIO.read(new File("/home/modzel101/Pictures/WhiteStone.png"));
			blackStone = ImageIO.read(new File("/home/modzel101/Pictures/BlackStone.png"));
	     	board = ImageIO.read(new File("/home/modzel101/Pictures/Board800V2.png"));
	     	
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
	public MenuPanel() {
		new JPanel (new BorderLayout());
		this.setSize(new Dimension(200,800));
		this.setBackground(Color.RED);
		JButton nowaGra = new JButton("Nowa Gra");
		this.add(nowaGra);
		JLabel punktyBialego = new JLabel("Punkty Białego: " + );
	}
	
}

