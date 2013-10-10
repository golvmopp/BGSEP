package host;

import java.awt.AWTException;
import java.awt.Robot;

public class Joystick extends Thread {
	private int joystickID;
	private float value;
	private boolean stopped;
	private final static int PERIOD = 100;
	private Robot robot;
	private int clientID;
	
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
	
	public void setStopped() {
		stopped = true;
	}
	
	private int getPressTime() {
		return Math.round((value * PERIOD));
	}
}
