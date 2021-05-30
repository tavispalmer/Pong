//Torbert, e-mail: mr@torbert.com, website: www.mr.torbert.com
//version 6.17.2003

import javax.swing.JFrame;
public class PongDriver
{
	public static void main(String[] args)
	{ 
		JFrame frame = new JFrame("Pong");
		frame.setSize(1000, 1000);
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new PongPanel());
		frame.setVisible(true);
	}
}