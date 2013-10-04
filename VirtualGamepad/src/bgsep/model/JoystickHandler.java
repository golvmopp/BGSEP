package bgsep.model;

import java.nio.ByteBuffer;

import android.widget.ImageView;

/**
 * Subclass of Joystick that handles joystick movements.
 * @author patrik
 *
 */

public class JoystickHandler extends Joystick {

	private int 	joystickID ;
	private float	prevPosX, prevPosY;
	private boolean leftRightEnabled, upDownEnabled;
	private boolean indicateKeyPress;
	
	private final float 	SENSITIVITY = 0.2f;
	
	/**
	 * Takes a positioned boundary ImageView and a positioned
	 * stick ImageView. Enabled by default.
	 * @param boundary an ImageView
	 */
	public JoystickHandler(ImageView boundary, ImageView stick) {
		super(boundary, stick);
		leftRightEnabled = upDownEnabled = false;
		prevPosX = prevPosY = 0;
		indicateKeyPress = false;
	}
	
	

	public void setLeftRightEnabled(boolean leftRightEnabled) {
		this.leftRightEnabled = leftRightEnabled;
	}



	public void setUpDownEnabled(boolean upDownEnabled) {
		this.upDownEnabled = upDownEnabled;
	}



	public int getJoystickID() {
		return joystickID;
	}

	public void setJoystickID(int joystickID) {
		this.joystickID = joystickID;
	}

	/**
	 * Determines Left/Right/Up/Down and sends a ByteBuffer to the observers.
	 * The first bit in the ByteBuffer indicates whether key should be pressed
	 * or released.
	 */
	@Override
	public void onStickMovement() {
		
		// Update views
		setChanged();
		notifyObservers();
		
		if(leftRightEnabled) {
			// Check to see if an update to server is necessary
			// according to the specified sensitivity
			float currPosX = getX();
			if((currPosX >= prevPosX + SENSITIVITY) || 
					(currPosX <= prevPosX - SENSITIVITY)) {
				prevPosX = currPosX;
			
				// Check to see if it should indicate a keyPress or keyRelease
				// and indicates a keyRelease
				if(indicateKeyPress && (currPosX > -SENSITIVITY && currPosX < SENSITIVITY)) {
					indicateKeyPress = false;
					notifyComm();
				}
				else {
					indicateKeyPress = true;
					notifyComm();							
				}
			}
		}
		
		if(upDownEnabled) {
			// Check to see if an update to server is necessary
			// according to the specified sensitivity
			float currPosY = getY();
			if((currPosY >= prevPosY + SENSITIVITY) || 
					(currPosY <= prevPosY - SENSITIVITY)) {
				prevPosY = currPosY;
			
				// Check to see if it should indicate a keyPress or keyRelease
				// and indicates a keyRelease
				if(indicateKeyPress && (currPosY > -SENSITIVITY && currPosY < SENSITIVITY)) {
					indicateKeyPress = false;
					notifyComm();
				}
				else {
					indicateKeyPress = true;
					notifyComm();							
				}
			}
		}
	}
	
	private void notifyComm() {
		setChanged();
		notifyObservers(indicateKeyPress);
	}
	
	public void setLeftRightEnabled() {
		leftRightEnabled = true;
	}
	
	public void setUpDownEnabled() {
		upDownEnabled = true;
	}

}
