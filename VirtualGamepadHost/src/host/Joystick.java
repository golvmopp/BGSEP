/*
   Copyright (C) 2013  Isak Eriksson, Patrik Wållgren

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package host;

import java.awt.AWTException;
import java.awt.Robot;

/**
 * @author Isak Eriksson (isak.eriksson@mail.com) & Patrik Wållgren ()
 * 
 *         This class simulates an analog stick, by pressing keys rapidly. A
 *         float value represents the percentage of the time in which the key is
 *         pressed. The key is pressed for a calculated amount of time and is
 *         then released for the remaining time of the PERIOD.
 * 
 */
public class Joystick extends Thread {
	private int joystickID;
	private float value;
	private boolean stopped;
	private final static int PERIOD = 100; // amount of time between each key
											// press
	private Robot robot;
	private int clientID;

	/**
	 * Constructs a new instance of Joystick.
	 * 
	 * @param joystickID
	 *            the ID of the button/joystick axis
	 * @param clientID
	 *            ID of the client that sent the joystick belongs to
	 */
	public Joystick(int joystickID, int clientID) {
		stopped = false;
		this.joystickID = joystickID;
		this.clientID = clientID;
		this.setName("Joystick" + clientID + "." + joystickID);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			System.out.println("unable to create robot");
		}
		start();
	}

	/**
	 * Sets how much the joystick is moved.
	 * 
	 * @param value
	 *            a float between 0 and 1 where 0 is released and 1 is fully
	 *            pressed.
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
				try {
					robot.keyPress(Configuration.getInstance().getKeyCode(clientID, joystickID));
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				}
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
				try {
					robot.keyRelease(Configuration.getInstance().getKeyCode(clientID, joystickID));
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				}
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
		if(value > 1.0)
			value = 1.0f;
		return Math.round((value * PERIOD));
	}
}
