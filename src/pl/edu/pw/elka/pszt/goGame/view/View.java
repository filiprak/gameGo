package pl.edu.pw.elka.pszt.goGame.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


import pl.edu.pw.elka.pszt.goGame.controller.Controller;
import pl.edu.pw.elka.pszt.goGame.model.Board;

public class View {
	JFrame frame;
	
	public void createWindow() throws IOException {
		//1. Create the frame.
		frame = new JFrame("FrameDemo");

		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		JPanel p = new JPanel(new BorderLayout());
		p.setSize(new Dimension(100,400));
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setSize(new Dimension(300,400));
		JButton b = new JButton("test");
	    
		controller = new Controller(this);
	    
	    pane = new GamePanel(controller, this);
		
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
	public void showResults() {
		mP.showResults();
	}
	public JFrame getFrame() {
		return this.frame;
	}
	
	GamePanel pane;
	MenuPanel mP;
	Controller controller;
}

class GamePanel extends JPanel {
	public GamePanel(Controller c, View v) {
		super();
		controller = c;
		playersTurn = true;
		view = v;
		addMouseListener(new MouseHandler());
		opponentThinks = false;
	}
	
	public void updatePanel() {
		 this.paintComponent(this.getGraphics());
	}
	
	 @Override
     protected void paintComponent(Graphics g) {
     	super.paintComponent(g);
     	BufferedImage whiteStone;
     	BufferedImage blackStone;
     	BufferedImage board;
     	BufferedImage hourglass;
		try {
			whiteStone = ImageIO.read(new File("whiteStone.png"));
			blackStone = ImageIO.read(new File("blackStone.png"));
	     	board = ImageIO.read(new File("board.png"));
	     	hourglass = ImageIO.read(new File("hourglass.png"));
	     	
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
	     	if( opponentThinks ) {
	     		g.drawImage(hourglass, 600, 600, null);
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
		 opponentThinks = true;
		 updatePanel();
		 controller.makeOpponentMove();
		 opponentThinks = false;
		 updatePanel();
	 }
	 
	 private int getPos(double x) {
		 if( x < STARTING_POINT) return -1;
		 int x_coordinate = (int)x;
		 x_coordinate -= STARTING_POINT;
		 
		 if( x_coordinate % STONES_DISTANCE > 80) return -1;
		 int pos = (int)(x_coordinate / STONES_DISTANCE);
		 return pos > 4 ? -1 : pos;
	 }
	 
	 
	 
	 private Controller controller;
	 private boolean playersTurn;
	 private View view;
	 private boolean opponentThinks;

	 final int STARTING_POINT = 40;
	 final int STONES_DISTANCE = 160;
  	

	/**
	 * class managing mouse actions
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
	 JLabel txt;
	 JButton newGame;
	 JButton pas;
	 JButton options;
	 JButton exit;
	
	Controller c;
	View view;

	

	public MenuPanel(Controller cc, View v) {
		super();
		c = cc;
		view = v;
		this.setPreferredSize(new Dimension(205,800));
		this.setBackground(Color.YELLOW);
		this.txt = new JLabel("                      GO");
		this.txt.setPreferredSize(new Dimension(200, 300));
		this.add(txt);
		this.newGame = new JButton("New Game");
		this.newGame.setPreferredSize(new Dimension(200,100));
		this.newGame.addActionListener(new mPActionListener());
		this.add(newGame);
		this.pas = new JButton("Pas");
		this.pas.setPreferredSize(new Dimension(200,100));
		this.pas.addActionListener(new mPActionListener());
		this.add(pas);
		this.options = new JButton("Options");
		this.options.setPreferredSize(new Dimension(200,100));
		this.options.addActionListener(new mPActionListener());
		this.add(options);
		this.exit = new JButton("Exit");
		this.exit.setPreferredSize(new Dimension(200,100));
		this.exit.addActionListener(new mPActionListener());
		this.add(exit);
	}
	
	public void showResults() {
		JFrame frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(500, 400));
		frame.setLayout(new BorderLayout());
		
        Toolkit zestaw = Toolkit.getDefaultToolkit();
		Dimension rozmiarEkranu = zestaw.getScreenSize();
		int wys = rozmiarEkranu.height;
		int szer = rozmiarEkranu.width;
		frame.setLocation(szer/3, wys/3);
		c.getCountPoints();
		if(c.getBlackPoints() < c.getWhitePoints()) {
			JLabel wW = new JLabel("<html>White wins! <br> White Points:<html>" + Double.toString(c.getWhitePoints()) + "<html><br> Black Points: <html>" + Double.toString(c.getBlackPoints()));
			frame.add(wW);
		}
		else if(c.getBlackPoints() > c.getWhitePoints()) {
			JLabel bW = new JLabel("<html>Black wins!<br> White Points:<html>" + Double.toString(c.getWhitePoints()) + "<html><br> Black Points: <html>" + Double.toString(c.getBlackPoints()));
			frame.add(bW);
		}
		else {
			JLabel dW = new JLabel("<html>Draw! <br> White Points:<html>" + Double.toString(c.getWhitePoints()) + "<html><br> Black Points: <html>" + Double.toString(c.getBlackPoints()));
			frame.add(dW);
		}
		frame.setVisible(true);
	}
	
	private class mPActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == newGame)
				c.newGame();
			else if(e.getSource() == pas) {
				c.makeMove(25);
				c.makeOpponentMove();
				view.updatePanel();
			}
			else if(e.getSource() == exit) {
				c.exitGame();	
			}
			else if(e.getSource() == options) {
				OptionsWindow ow = new OptionsWindow(c, 800, 600);
			}
		}	
	}
}


class OptionsWindow
{
	 /**
	 * Responsible for creating a window with options.
	 */
	private JFrame frame;
	/**
	 * connects with a controller responsible for a logic of an application.
	 */
	private Controller controller;
	/**
	 * button which saves changes, sends them to a controller and closes a window.
	 */
	private JButton save;
	/**
	 * button which abandons changes and closes a window.
	 */
	private JButton cancel;
	
	JSpinner simulationNumber;
	JSpinner childrenLimit;
	JSpinner childrenLimitJump;
	JSpinner explorationRatio;
	JSpinner komiPoints;
	JSpinner treeDepth;
	JSpinner simulationsPerNode;
	
	JCheckBox playWhite;
	JCheckBox chooseParent;
	JCheckBox decreaseLimit;
	JCheckBox winOnlyRatio;
	
	public OptionsWindow(Controller c, int width, int height) 
	{
		frame = new JFrame("Options");
		frame.setResizable(false);
		controller = c;
		frame.setLayout(new GridLayout(0,1));
		AIOptions options = controller.getOptions();
		
		simulationNumber = createSpinner(options.simulations, 10000, 5000000, 1000);
		frame.add(createPanel("Number of simulations: ", simulationNumber));
		
		simulationsPerNode = createSpinner(options.simulationsPerNode, 1, 1000, 1);
		frame.add(createPanel("Simulations per node: ", simulationsPerNode));
		
		childrenLimit = createSpinner(options.children_limit, 10000, 5000000, 1000);
		frame.add(createPanel("Maximum simulations per node: ", childrenLimit));
		
		childrenLimitJump = createSpinner(options.children_limit_jump, 1, 25, 1);
		frame.add(createPanel("Tree broad ratio: ", childrenLimitJump));
		
		explorationRatio = createSpinner(options.exploration_ratio, 0, 5, 0.01);
		frame.add(createPanel("Exploration evaluation: ", explorationRatio));
		
		treeDepth = createSpinner(options.treeDepth, 1, 30, 1);
		frame.add(createPanel("Maximum tree depth: ", treeDepth));
		
		komiPoints = createSpinner(options.komi_points, 0, 25, 1.0);
		frame.add(createPanel("Komi points", komiPoints));
		
		decreaseLimit = new JCheckBox();
		decreaseLimit.setSelected(options.decreasingLimit);
		frame.add(createPanel("Decrease tree broad ratio with every move: ", decreaseLimit));
		
		playWhite = new JCheckBox();
		playWhite.setSelected(options.newGameColor == Board.WHITESGN);
		frame.add(createPanel("Start as white: ", playWhite));
		
		winOnlyRatio = new JCheckBox();
		winOnlyRatio.setSelected(options.winOnlyRatio);
		frame.add(createPanel("Calculate ratio from wins only: ", winOnlyRatio));
		
		chooseParent = new JCheckBox();
		chooseParent.setSelected(options.exploreParent);
		frame.add(createPanel("Choose parent over children: ", chooseParent));
		
		save = new JButton("Save");
		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				controller.setOptions(createMessage());
				closeFrame();
			}		
		});	
		
		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{		
			public void actionPerformed(ActionEvent event)
			{
				closeFrame();
			}	
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(save);
		buttonPanel.add(cancel);
		frame.add(buttonPanel);
		
		
		frame.setSize(width, height);
		frame.setVisible(true);
	}
		
	/**
	 *	Function responsible for closing this window. 
	 */
	private void closeFrame()
	{
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	/**
	 * Creates and returns spinner with given parameters.
	 * @param cur starting position of spinner
	 * @param mn minimal value of spinner
	 * @param mx maximal value of spinner
	 * @param stp value of a single step
	 * @return created spinner
	 */
	private JSpinner createSpinner(double cur, double mn, double mx, double stp)
	{
		JSpinner m_numberSpinner;
	    SpinnerNumberModel m_numberSpinnerModel;
	    Double current = new Double(cur);
	    Double min = new Double(mn);
	    Double max = new Double(mx);
	    Double step = new Double(stp);
	    m_numberSpinnerModel = new SpinnerNumberModel(current, min, max, step);
	    m_numberSpinner = new JSpinner(m_numberSpinnerModel);
	    m_numberSpinner.setPreferredSize(new Dimension(100, 25));
	    return m_numberSpinner;
	}
	
	/**
	 * Creates JPanel with a labeled JComponent
	 * @param label value to be displayed as a label
	 * @param comp component to be added to a panel
	 * @return created panel with added elements
	 */
	private JPanel createPanel(String label, JComponent comp)
	{
		JPanel panel = new JPanel();
		panel.add(new JLabel(label));
		panel.add(comp);
		return panel;
	}
	
	/**
	 * Creates a message for a controller to send him options user set.
	 * @return message filled with options, ready to be sent to a controller
	 */
	private AIOptions createMessage()
	{
		AIOptions options = new AIOptions();
		options.simulations = ((Double)(simulationNumber.getValue())).intValue();
		options.children_limit = ((Double)childrenLimit.getValue()).intValue();
		options.children_limit_jump = ((Double)childrenLimitJump.getValue()).intValue();
		options.exploration_ratio = (Double)explorationRatio.getValue();
		options.komi_points = (Double)komiPoints.getValue();
		options.newGameColor = playWhite.isSelected() ? Board.WHITESGN : Board.BLACKSGN;
		options.treeDepth = ((Double)treeDepth.getValue()).intValue();
		options.simulationsPerNode = ((Double)simulationsPerNode.getValue()).intValue();
		options.exploreParent = chooseParent.isSelected();
		options.decreasingLimit = decreaseLimit.isSelected();
		options.winOnlyRatio = winOnlyRatio.isSelected();
		return options;
	}
}







