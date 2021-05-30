// Billington.  email: mlbillington@fcps.edu
// version: 7.25.2007

import java.awt.*;
public class Ball extends Polkadot
{
	private int dx;       // pixels to move each time step() is called.
	private int dy;
	private double speed;
	private double angle;
	// constructors
	public Ball()         //default constructor
	{
		super(500,500,50,Color.white);
		speed = 16;
		//angle = Math.random() * 2 * Math.PI;
		//dx = (int)(speed * Math.cos(angle));          //pixels to move vertically
		//dy = (int)(speed * Math.sin(angle));          //pixels to move sideways
	}
	public Ball(int x, int y, int dia, Color c)
	{
		super(x, y, dia, c);
		speed = 16;
		//angle = Math.random() * 2 * Math.PI;
		//dx = (int)(speed * Math.cos(angle));
		//dy = (int)(speed * Math.sin(angle));
	}
	
	public void reset() {
		this.setX(500);
		this.setY(500);
		this.setDiameter(50);
		this.setColor(Color.white);
		//angle = Math.random() * 2 * Math.PI;
		//dx = (int)(speed * Math.cos(angle));          //pixels to move vertically
		//dy = (int)(speed * Math.sin(angle)); 
	}
	public void reset(int x, int y, int dia, Color c) {
		this.setX(x);
		this.setY(y);
		this.setDiameter(dia);
		this.setColor(c);
	}
	//modifier methods 
	public void setdx(int x)        
	{
		dx = x;
	}
	public void setdy(int y)
	{
		dy = y;
	}
	public void setSpeed(double aSpeed) {
		speed = aSpeed;
	}
	public void setAngle(double aAngle) {
		angle = aAngle;
	}
	//accessor methods
	public int getdx()             
	{
		return dx;
	}
	public int getdy()
	{
		return dy;
	}
	public double getSpeed() {
		return speed;
	}
	public double getAngle() {
		return angle;
	}
	//instance methods
	public void move(int rightEdge, int bottomEdge)
	{
		setX(getX()+ dx);                    // x = x + dx
		setY(getY() + dy);

		//***Update the Y value with the change in Y*****////


		int radius = getDiameter() / 2;
		/*if(getX() >= rightEdge - radius) {    //hits the right edge
			setX(rightEdge - radius);
			dx = dx * -1; 
		}*/
		/*if(getX() <= radius) {	//hits the left edge
			setX(radius);
			dx = dx * -1;
		}*/
		if(getY() >= bottomEdge - radius) {	//hits the bottom edge
			setY(bottomEdge - radius);
			dy = dy * -1;
		}
		if(getY() <= radius) {	//hits the top edge
			setY(radius);
			dy = dy * -1;
		}

		/******
   		complete the other 3 edges


		 *****/


	}
}