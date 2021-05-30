// Name: 				Date:

import java.awt.*;

public class Bumper
{
	//private fields, all ints, for a Bumper
	//hint: the "location" of the bumper begins at its top left corner.      
	private int myX;
	private int myY;
	private int myXWidth;
	private int myYWidth;
	private Color myColor;
	
	// Default constructor
	public Bumper()         
	{
		myX = 500;
		myY = 250;
		myXWidth = 50;
		myYWidth = 250;
		myColor = Color.white;
	}

	// 5-arg constructor
	public Bumper(int x, int y, int xWidth, int yWidth, Color c)
	{
		myX = x;
		myY = y;
		myXWidth = xWidth;
		myYWidth = yWidth;
		myColor = c;
	}
	
	//like a constructor, makes an easy way to reset the game
	public void reset() {
		myX = 500;
		myY = 250;
		myXWidth = 50;
		myYWidth = 250;
		myColor = Color.white;
	}
	//5 arg reset; see above
	public void reset(int x, int y, int width, int yWidth, Color c) {
		myX = x;
		myY = y;
		myXWidth = width;
		myYWidth = yWidth;
		myColor = c;
	}

	//***********************************************
	//
	//  Accessor methods  (one for each field)
	//
	//***********************************************

	public int getX() {
		return myX;
	}
	public int getY() {
		return myY;
	}
	public int getXWidth() {
		return myXWidth;
	}
	public int getYWidth() {
		return myYWidth;
	}
	public Color getColor() {
		return myColor;
	}

	//***********************************************
	//
	//  Modifier methods  (one for each field)
	//
	//***********************************************   

	public void setX(int x) {
		myX = x;
	}
	public void setY(int y) {
		myY = y;
	}
	public void setXWidth(int xWidth) {
		myXWidth = xWidth;
	}
	public void setYWidth(int yWidth) {
		myYWidth = yWidth;
	}
	public void setColor(Color c) {
		myColor = c;
	}

	//************************
	//
	// Instance methods
	//
	//************************


	/**
	 * Chooses a random (x,y) location for the Bumper.  Bumper stays entirely in the window.
	 * @param rightEdge the right side of the window
	 * @param bottomEdge the bottom side of the window
	 */
	public void jump(int rightEdge, int bottomEdge) {
		myX = (int)(Math.random()* rightEdge);
		myY = (int)(Math.random()* bottomEdge);
	}

	/**
	 * Draws a rectangular bumper on the buffer
	 * @param myBuffer the picture drawn on the screen
	 */
	public void draw(Graphics myBuffer) {
		myBuffer.setColor(myColor);
		myBuffer.fillRect(myX, myY, myXWidth, myYWidth);
	}   
	
	/**
	 * Returns true if any part of the Polkadot is inside the bumper
	 * @return true if any part of the polkadot is inside the bumper
	 */
	public boolean inBumper(Polkadot dot) {
		for(int x = getX(); x <= getX() + getXWidth(); x++)   //starts at upper left corner(x,y)
			for(int y = getY(); y <= getY() + getYWidth(); y++)
				if(distance(x, y, dot.getX(), dot.getY()) <= dot.getRadius() ) //checks every point on the bumper
					return true;            
		return false;
	}  

	/**
	 * Calculates the distance between (x1, y1) and (x2, y2)
	 * @param x1 Comment...
	 * @param y1 Comment...
	 * @param x2 Comment...
	 * @param y2 Comment...
	 * @return the distance between (x1, y1) and (x2, y2)
	 */
	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}	
}