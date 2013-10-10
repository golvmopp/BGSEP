package host;

import java.awt.AWTException;
import java.awt.Robot;

/**
 * @author Isak Eriksson (isak.eriksson@mail.com) & Patrik Wållgren ()
 *
 * This class simulates an analog stick, by pressing keys rapidly.
 * A float value represents the percentage of the time in which the key is pressed.
 * The key is pressed for a calculated amount of time and is then released for the
 * remaining time of the PERIOD.
 * 
 */
public class Joystick extends Thread {
	private int joystickID;
	private float value;
	private boolean stopped;
	private final static int PERIOD = 100; //amount of time between each key press
	private Robot robot;
	private int clientID;
	
	/**
	 * Constructs a new instance of Joystick. 
	 * @param joystickID the ID of the button/joystick axis
	 * @param clientID ID of the client that sent the joystick belongs to
	 */
	public Joystick(int joystickID, int clientID) {
		stopped = false;
		this.joystickID = joystickID;
		this.clientID = clientID;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.out.println("unable to create robot");
			e.printStackTrace();
		}
		start();
	}
	
	/**
	 * Sets how much the joystick is moved.
	 * @param value a float between 0 and 1 where 0 is released and 1 is fully pressed.
	 */
	public void setNewValue(float value) {
		this.value = value;
	}
	
	@Override
	public void run() {
		int sleepTime;
		int pressTime;
		boolean skip = true;
		while (!interrupted() && !stopped) {
			pressTime = getPressTime();
			sleepTime = PERIOD - pressTime;
			if (pressTime > PERIOD * 0.05) {
				skip = false;
				robot.keyPress(Configuration.getInstance().getKeyCode(clientID, joystickID));
			} else {
				skip = true;
			}
			try {
				Thread.sleep(pressTime);
			} catch (InterruptedException e) {
				System.out.println("unable to sleep, not tired");
				e.printStackTrace();
			}
			if (!skip) {
				robot.keyRelease(Configuration.getInstance().getKeyCode(clientID, joystickID));
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println("don't interrupt me while i am trying to sleep!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stops the thread.
	 */
	public void setStopped() {
		stopped = true;
	}
	
	private int getPressTime() {
		return Math.round((value * PERIOD));
	}
}
