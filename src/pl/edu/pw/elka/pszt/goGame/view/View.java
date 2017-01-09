package pl.edu.pw.elka.pszt.goGame.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
	    final BufferedImage image = ImageIO.read(new File("/home/gepard/Board1000.png"));

	    JPanel pane = new JPanel() {
	        @Override
            protected void paintComponent(Graphics g) {
	        	super.paintComponent(g);
	            g.drawImage(image, 0, 0, null);
	        }
	    };


	    frame.add(pane, BorderLayout.CENTER);
	    
		
		//4. Size the frame.
		frame.setSize(new Dimension(1100, 1000));
		//frame.pack();

		//5. Show it.
		frame.setVisible(true);
	}
}
