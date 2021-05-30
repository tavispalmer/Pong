//Torbert, e-mail: mr@torbert.com, website: www.mr.torbert.com
//version 6.17.2003
// Chapin, email: john.chapin@lcps.org changed to use sleep and Gui class

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class PongPanel extends JPanel {
	// constants
	private static final int FRAME = 1000;
	private static final Color BACKGROUND = Color.black;

	// fields
	private BufferedImage myImage;
	private Ball ball;
	private Bumper leftBumper;    
	private Bumper rightBumper;
	private int[] score = new int[2];
	private int speed = 10; //speed of the bumpers
	
	//handles ball serving (before it starts moving)
	private boolean ballServed = false;
	private int bumperServing = 0; //0 = left; 1 = right
	
	//inputs
	private static boolean rightUp = false;
	private static boolean rightDown = false;
	private static boolean rightServe = false;
	private static boolean leftUp = false;
	private static boolean leftDown = false;
	private static boolean leftServe = false;
	private static boolean enter = false;
	private static boolean escape = false;
	private static boolean rightArrow = false;
	private static boolean aiUp = false;
	private static boolean aiDown = false;
	private static boolean aiServe = false;
	
	//detects when button is initially pressed; index 0 = pressed; index 1 = released
	private static boolean[] rightUpPressed = new boolean[2];
	private static boolean[] rightDownPressed = new boolean[2];
	private static boolean[] rightServePressed = new boolean[2];
	private static boolean[] leftUpPressed = new boolean[2];
	private static boolean[] leftDownPressed = new boolean[2];
	private static boolean[] leftServePressed = new boolean[2];
	private static boolean[] enterPressed = new boolean[2];
	private static boolean[] escapePressed = new boolean[2];
	private static boolean[] rightArrowPressed = new boolean[2];
	
	//game states; 0 = title; 1 = singleplayer; 2 = multiplayer
	private int gameState = 0;
	private int titleOption = 0; //which mode is selected on title screen
	private boolean options = false;
	private boolean controls = false;
	private boolean gameOver = false;
	private int gameOverTimer = 0; //makes the text flash after a little while

	private int delayLength = 10;

	public PongPanel() {
		getPanelGraphics();

		ball = new Ball();
		leftBumper = new Bumper(50, 375, 50, 250, Color.white);
		rightBumper = new Bumper(900, 375, 50, 250, Color.white);
		
		for(int index = 0; index < score.length; index++)
			score[index] = 0;

		/*
		 * Create a thread that will call the Panel every time
		 * the screen needs to be drawn.
		 */
		PongPanelGui ballGui = new PongPanelGui(this);
		Thread ballThread = new Thread(ballGui);
		ballThread.start();
		addKeyListener(new Key());
		setFocusable(true);
	}

	private void drawBall() {

		Graphics graphics = getPanelGraphics();
		
		//check if the ball goes off the screen
		if(ball.getX() + ball.getRadius() < 0) {
			score[1] += 1;
			ballServed = false;
			bumperServing = 0;
		}
		else if(ball.getX() - ball.getRadius() > FRAME) {
			score[0] += 1;
			ballServed = false;
			bumperServing = 1;
		}
		
		//Move the ball
		if(ballServed) {
			ball.move(FRAME, FRAME);
		}
		else if(bumperServing == 0) {
			ball.setX(leftBumper.getX() + leftBumper.getXWidth() + ball.getRadius());
			ball.setY(leftBumper.getY() + leftBumper.getYWidth()/2);
		}
		else {
			ball.setX(rightBumper.getX() - ball.getRadius());
			ball.setY(rightBumper.getY() + rightBumper.getYWidth()/2);
		}
		
		//Move the bumper if a key is pressed
		if(rightUp)
			rightBumper.setY(rightBumper.getY()-speed);
		if(rightDown)
			rightBumper.setY(rightBumper.getY()+speed);
		if(gameState == 2) { //if this is a multiplayer game
			if(leftUp)
				leftBumper.setY(leftBumper.getY()-speed);
			if(leftDown)
				leftBumper.setY(leftBumper.getY()+speed);
		}
		else if(gameState == 1) { //if this is singleplayer
			runAi();
			if(aiUp)
				leftBumper.setY(leftBumper.getY()-speed);
			if(aiDown)
				leftBumper.setY(leftBumper.getY()+speed);
		}
		
		//make sure the bumper doesn't go off the screen
		if(leftBumper.getY() < 0)
			leftBumper.setY(0);
		if(leftBumper.getY() + leftBumper.getYWidth() > FRAME)
			leftBumper.setY(FRAME - leftBumper.getYWidth());
		if(rightBumper.getY() < 0)
			rightBumper.setY(0);
		if(rightBumper.getY() + rightBumper.getYWidth() > FRAME)
			rightBumper.setY(FRAME - rightBumper.getYWidth());
		
		//Bumper collision
		if(ballServed) {
			BumperCollision.collide(leftBumper, ball);
			BumperCollision.collide(rightBumper, ball);
		}
		
		//Graphics stuff
		ball.draw(graphics);
		leftBumper.draw(graphics);
		rightBumper.draw(graphics);
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("Serif", Font.BOLD, 75));
		graphics.drawString(score[0] + " - " + score[1], 400, 100);
		repaint();
		
		//calculates the angle for the ball when served
		if(!ballServed) {
			if(bumperServing == 0 && leftServe && gameState == 2 ||
					bumperServing == 0 && aiServe && gameState == 1) {
				ballServed = true;
				if(leftBumper.getY() + leftBumper.getYWidth()/2 < FRAME/2) {
					ball.setdx((int)(ball.getSpeed() * Math.cos(1.75 * Math.PI)));
					ball.setdy((int)(ball.getSpeed() * -1 *  Math.sin(1.75 * Math.PI)));
				}
				else {
					ball.setdx((int)(ball.getSpeed() * Math.cos(0.25 * Math.PI)));
					ball.setdy((int)(ball.getSpeed() * -1 * Math.sin(0.25 * Math.PI)));
				}
			}
			else if(bumperServing == 1 && rightServe) {
				ballServed = true;
				if(rightBumper.getY() + rightBumper.getYWidth()/2 < FRAME/2) {
					ball.setdx((int)(ball.getSpeed() * Math.cos(1.25 * Math.PI)));
					ball.setdy((int)(ball.getSpeed() * -1 * Math.sin(1.25 * Math.PI)));
				}
				else {
					ball.setdx((int)(ball.getSpeed() * Math.cos(0.75 * Math.PI)));
					ball.setdy((int)(ball.getSpeed() * -1 * Math.sin(0.75 * Math.PI)));
				}
			}
		}
		
		//game over when score reaches 10
		if(score[0] >= 10 || score[1] >= 10) {
			gameOver = true;
		}
		
		//resets the game when you press escape; basically a soft reset
		if(escape) {
			gameState = 0;
			titleOption = 0;
			reset();
		}
	}
	
	private void drawGameOver() {
		Graphics graphics = getPanelGraphics();
		
		//Move the bumper if a key is pressed
		if(rightUp)
			rightBumper.setY(rightBumper.getY()-speed);
		if(rightDown)
			rightBumper.setY(rightBumper.getY()+speed);
		if(gameState == 2) { //if this is a multiplayer game
			if(leftUp)
				leftBumper.setY(leftBumper.getY()-speed);
			if(leftDown)
				leftBumper.setY(leftBumper.getY()+speed);
		}
		
		//make sure the bumper doesn't go off the screen
		if(leftBumper.getY() < 0)
			leftBumper.setY(0);
		if(leftBumper.getY() + leftBumper.getYWidth() > FRAME)
			leftBumper.setY(FRAME - leftBumper.getYWidth());
		if(rightBumper.getY() < 0)
			rightBumper.setY(0);
		if(rightBumper.getY() + rightBumper.getYWidth() > FRAME)
			rightBumper.setY(FRAME - rightBumper.getYWidth());
		
		//keeps the bumpers and score on the screen
		leftBumper.draw(graphics);
		rightBumper.draw(graphics);
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("Serif", Font.BOLD, 75));
		graphics.drawString(score[0] + " - " + score[1], 400, 100);
		
		if(gameOverTimer%(delayLength*8) < delayLength*4) {
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Serif", Font.BOLD, 75));
			graphics.drawString("GAME OVER", 250, 450);
			graphics.drawString("PRESS ESCAPE", 225, 650);
			if(score[0] > score[1])
				graphics.drawString("PLAYER 1 WINS", 200, 550);
			else if(score[1] > score[0])
				graphics.drawString("PLAYER 2 WINS", 200, 550);
			else
				graphics.drawString("TIED GAME", 300, 550);
		}
		
		repaint();
		
		gameOverTimer++;
		if(gameOverTimer >= 80)
			gameOverTimer = 0;
		
		//restarts your current game if enter is pressed
		if(enter) {
			reset();
		}
		
		//resets the game when you press escape; basically a soft reset
		if(escape) {
			gameState = 0;
			titleOption = 0;
			reset();
		}
	}
	
	private void drawTitle() { //remove commented out code to add options
		Graphics graphics = getPanelGraphics();
		
		if(rightUpPressed[0]) {
			titleOption--;
			if(titleOption < 0)
				titleOption = 1; //change to 2 for options
		}
		if(rightDownPressed[0]) {
			titleOption++;
			if(titleOption > 1) //change to 2 for options
				titleOption = 0;
		}
		if(enterPressed[0] && titleOption == 0) {
			gameState = 1;
			controls = true;
		}
		if(enterPressed[0] && titleOption == 1) {
			gameState = 2;
			controls = true;
		}
		/*if(enterPressed[0] && titleOption == 2) {
			options = true;
			titleOption = 0;
		}*/
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("Serif", Font.BOLD, 200));
		graphics.drawString("PONG", 200, 300);
		
		if(titleOption == 0) {
			graphics.setColor(Color.yellow);
			graphics.setFont(new Font("Serif", Font.BOLD, 50));
			graphics.drawString("1 Player", 400, 600);
			graphics.setColor(Color.white);
			graphics.drawString("2 Players", 400, 700);
			//graphics.drawString("Options", 400, 800);
		}
		else if(titleOption == 1) {
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Serif", Font.BOLD, 50));
			graphics.drawString("1 Player", 400, 600);
			graphics.setColor(Color.yellow);
			graphics.drawString("2 Players", 400, 700);
			//graphics.setColor(Color.white);
			//graphics.drawString("Options", 400, 800);
		}
		/*else if(titleOption == 2) {
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Serif", Font.BOLD, 50));
			graphics.drawString("1 Player", 400, 600);
			graphics.drawString("2 Players", 400, 700);
			graphics.setColor(Color.yellow);
			graphics.drawString("Options", 400, 800);
		}*/
		repaint();
	}
	
	private void drawControls() {
		Graphics graphics = getPanelGraphics();
		
		graphics.setColor(Color.white);
		graphics.setFont(new Font("Serif", Font.BOLD, 150));
		graphics.drawString("Controls", 200, 200);
		
		graphics.setFont(new Font("Serif", Font.PLAIN, 40));
		graphics.drawString("Left Bumper:", 100, 350);
		graphics.drawString("W: Move Up", 100, 450);
		graphics.drawString("S: Move Down", 100, 550);
		graphics.drawString("D: Serve Ball", 100, 650);
		graphics.drawString("Right Bumper:", 500, 350);
		graphics.drawString("Up Arrow: Move Up", 500, 450);
		graphics.drawString("Down Arrow: Move Down", 500, 550);
		graphics.drawString("Left Arrow: Serve Ball", 500, 650);
		
		if(enterPressed[0])
			controls = false;
		if(escapePressed[0]) {
			gameState = 0;
			titleOption = 0;
			controls = false;
			reset();
		}
		
		repaint();
	}
	
	private void drawOptions() {
		Graphics graphics = getPanelGraphics();
		
		if(rightUpPressed[0]) {
			titleOption--;
			if(titleOption < 0)
				titleOption = 2;
		}
		if(rightDownPressed[0]) {
			titleOption++;
			if(titleOption > 2)
				titleOption = 0;
		}
		
		if(titleOption == 0) {
			if(rightServePressed[0]) {
				ball.setSpeed(ball.getSpeed() - 1);
				if(ball.getSpeed() < 0)
					ball.setSpeed(0);
			}
			if(rightArrowPressed[0]) {
				ball.setSpeed(ball.getSpeed() + 1);
				if(ball.getSpeed() > 50)
					ball.setSpeed(50);
			}
		}
		else if(titleOption == 1) {
			if(rightServePressed[0]) {
				speed--;
				if(speed < 1)
					speed = 1;
			}
			if(rightArrowPressed[0]) {
				speed++;
				if(speed > 50)
					speed = 50;
			}
		}
		else if(titleOption == 2) {
			if(rightServePressed[0]) {
				ball.setDiameter(ball.getDiameter() - 1);
				if(ball.getDiameter() < 1)
					ball.setDiameter(1);
			}
			if(rightArrowPressed[0]) {
				ball.setDiameter(ball.getDiameter() + 1);
				if(ball.getDiameter() > 200)
					ball.setDiameter(200);
			}
			new Polkadot(700, 485, ball.getDiameter(), ball.getColor()).draw(graphics);
		}
				
		graphics.setColor(Color.white);
		graphics.setFont(new Font("Serif", Font.BOLD, 150));
		graphics.drawString("Options", 250, 200);
		
		if(titleOption == 0) {
			graphics.setColor(Color.yellow);
			graphics.setFont(new Font("Serif", Font.BOLD, 50));
			graphics.drawString("Ball Speed: " + ball.getSpeed(), 200, 300);
			graphics.setColor(Color.white);
			graphics.drawString("Bumper Speed: " + speed, 200, 400);
			graphics.drawString("Ball Size: " + ball.getDiameter(), 200, 500);
		}
		else if(titleOption == 1) {
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Serif", Font.BOLD, 50));
			graphics.drawString("Ball Speed: " + ball.getSpeed(), 200, 300);
			graphics.drawString("Ball Size: " + ball.getDiameter(), 200, 500);
			graphics.setColor(Color.yellow);
			graphics.drawString("Bumper Speed: " + speed, 200, 400);
		}
		else if(titleOption == 2) {
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Serif", Font.BOLD, 50));
			graphics.drawString("Ball Speed: " + ball.getSpeed(), 200, 300);
			graphics.drawString("Bumper Speed: " + speed, 200, 400);
			graphics.setColor(Color.yellow);
			graphics.drawString("Ball Size: " + ball.getDiameter(), 200, 500);
		}
		
		repaint();
		
		if(escapePressed[0] || enterPressed[0]) {
			gameState = 0;
			titleOption = 0;
			reset();
		}
	}
	
	//ai in singleplayer
	public void runAi() { //this ai will try to keep it's y value the same as the ball
		if(ballServed || !ballServed && bumperServing == 1) {//if the ball was served
			if(leftBumper.getY() + (leftBumper.getYWidth()/2) > ball.getY() &&
					leftBumper.getY() + (leftBumper.getYWidth()/2) - ball.getY() > leftBumper.getY() + (leftBumper.getYWidth()/2) - speed - ball.getY()) {
				aiDown = false;
				aiUp = true;
			}
			else if(leftBumper.getY() + (leftBumper.getYWidth()/2) < ball.getY() &&
					ball.getY() - leftBumper.getY() + (leftBumper.getYWidth()/2) < ball.getY() - leftBumper.getY() + (leftBumper.getYWidth()/2) + speed) {
				aiUp = false;
				aiDown = true;
			}
		}
		else if(bumperServing == 0) {
			aiServe = true;
		}
	}
	
	//resets the current game state (either 1 or 2)
	public void reset() {
		ball.reset(500, 500, ball.getDiameter(), ball.getColor());
		leftBumper.reset(50, 375, 50, 250, Color.white);
		rightBumper.reset(900, 375, 50, 250, Color.white);
		for(int index = 0; index < score.length; index++)
			score[index] = 0;
		ballServed = false;
		bumperServing = 0;
		gameOver = false;
		gameOverTimer = 0;
		options = false;
	}
	
	//getters
	public static boolean getRightUp() {
		return rightUp;
	}
	public static boolean getRightDown() {
		return rightDown;
	}
	public static boolean getRightServe() {
		return rightServe;
	}
	public static boolean getLeftUp() {
		return leftUp;
	}
	public static boolean getLeftDown() {
		return leftDown;
	}
	public static boolean getLeftServe() {
		return leftServe;
	}
	public static boolean getEnter() {
		return enter;
	}
	public static boolean getEscape() {
		return escape;
	}
	public static boolean getRightArrow() {
		return rightArrow;
	}
	public static boolean getAiUp() {
		return aiUp;
	}
	public static boolean getAiDown() {
		return aiDown;
	}
	public static boolean getAiServe() {
		return aiServe;
	}
	
	//setters
	public static void setRightUp(boolean input) {
		rightUp = input;
		if(input)
			rightUpPressed[0] = true;
		else
			rightUpPressed[1] = true;
	}
	public static void setRightDown(boolean input) {
		rightDown = input;
		if(input)
			rightDownPressed[0] = true;
		else
			rightDownPressed[1] = true;
	}
	public static void setRightServe(boolean input) {
		rightServe = input;
		if(input)
			rightServePressed[0] = true;
		else
			rightServePressed[1] = true;
	}
	public static void setLeftUp(boolean input) {
		leftUp = input;
		if(input)
			leftUpPressed[0] = true;
		else
			leftUpPressed[1] = true;
	}
	public static void setLeftDown(boolean input) {
		leftDown = input;
		if(input)
			leftDownPressed[0] = true;
		else
			leftDownPressed[1] = true;
	}
	public static void setLeftServe(boolean input) {
		leftServe = input;
		if(input)
			leftServePressed[0] = true;
		else
			leftServePressed[1] = true;
	}
	public static void setEnter(boolean input) {
		enter = input;
		if(input)
			enterPressed[0] = true;
		else
			enterPressed[1] = true;
	}
	public static void setEscape(boolean input) {
		escape = input;
		if(input)
			escapePressed[0] = true;
		else
			escapePressed[1] = true;
	}
	public static void setRightArrow(boolean input) {
		rightArrow = input;
		if(input)
			rightArrowPressed[0] = true;
		else
			rightArrowPressed[1] = true;
	}
	public static void setAiUp(boolean input) {
		aiUp = input;
	}
	public static void setAiDown(boolean input) {
		aiDown = input;
	}
	public static void setAiServe(boolean input) {
		aiServe = input;
	}
	
	public void resetPressed() {
		rightUpPressed[0] = false;
		rightUpPressed[1] = false;
		rightDownPressed[0] = false;
		rightDownPressed[1] = false;
		rightServePressed[0] = false;
		rightServePressed[1] = false;
		leftUpPressed[0]= false;
		leftUpPressed[1]= false;
		leftDownPressed[0] = false;
		leftDownPressed[1] = false;
		leftServePressed[0] = false;
		leftServePressed[1] = false;
		enterPressed[0] = false;
		enterPressed[1] = false;
		escapePressed[0] = false;
		escapePressed[0] = false;
		rightArrowPressed[0] = false;
		rightArrowPressed[1] = false;
	}

	private class Key extends KeyAdapter
	{	
		public void keyPressed(KeyEvent e)
		{

			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				PongPanel.setRightUp(true);
				//PongPanel.setRightDown(false);
			}

			if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				//PongPanel.setRightUp(false);
				PongPanel.setRightDown(true);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				PongPanel.setRightServe(true);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				PongPanel.setRightArrow(true);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_W) {
				PongPanel.setLeftUp(true);
				//PongPanel.setLeftDown(false);
			}
			
			if(e.getKeyCode() == KeyEvent.VK_S) {
				//PongPanel.setLeftUp(false);
				PongPanel.setLeftDown(true);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_D) {
				PongPanel.setLeftServe(true);
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				PongPanel.setEnter(true);
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				PongPanel.setEscape(true);
			}
		}//keyPressed

		public void keyReleased( KeyEvent e ) {
			if (e.getKeyCode() == KeyEvent.VK_UP)
			{
				PongPanel.setRightUp(false);
			}

			if (e.getKeyCode() == KeyEvent.VK_DOWN)
			{
				PongPanel.setRightDown(false);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				PongPanel.setRightServe(false);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				PongPanel.setRightArrow(false);
			}
			
			if (e.getKeyCode() == KeyEvent.VK_W) {
				PongPanel.setLeftUp(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				PongPanel.setLeftDown(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				PongPanel.setLeftServe(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				PongPanel.setEnter(false);
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				PongPanel.setEscape(false);
			}
		}//keyreleased
	}




	public void paintComponent(Graphics g) {
		g.drawImage(myImage, 0, 0, getWidth(), getHeight(), null);
	}

	/*
	 * set up the buffer if null create a new one, else clear the buffer with a
	 * background image
	 */
	private Graphics getPanelGraphics() {

		if (null == myImage) {
			myImage = new BufferedImage(FRAME, FRAME, BufferedImage.TYPE_INT_RGB);
		}
		int w = FRAME;
		int h = FRAME;

		Graphics g = myImage.getGraphics();
		g.setColor(BACKGROUND);// blue

		// 1 ********* write the code to "clear the screen" for animation
		g.fillRect(0, 0, w,h);
		return g;
	}

	public void updateBall()  {
		// draw the Ball and then sleep for "delay length" 
		try {
			SwingUtilities.invokeAndWait(
					new Runnable() {
						@Override
						public void run() {
							if(gameState == 0 && !options)
								drawTitle();
							else if(gameState == 0 && options)
								drawOptions();
							else if(controls)
								drawControls();
							else if (!gameOver)
								drawBall();
							else
								drawGameOver();
							resetPressed();
							try {
								Thread.sleep(delayLength);
							} catch (InterruptedException ex) {
								Thread.currentThread().interrupt();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
