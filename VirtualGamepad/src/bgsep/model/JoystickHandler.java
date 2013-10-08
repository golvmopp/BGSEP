package bgsep.model;

import android.widget.ImageView;
import bgsep.communication.CommunicationNotifier;

/**
 * Subclass of Joystick that handles joystick movements.
 * @author patrik
 *
 */

public class JoystickHandler extends Joystick {

	private int 	stickLeftID, stickRightID, stickUpID, stickDownID;
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
		stickLeftID = stickRightID = stickUpID = stickDownID = 0;
	}
	

	public void setLeftRightJoystickID(int left, int right) {
		stickLeftID 		= left;
		stickRightID 		= right;
		leftRightEnabled 	= true;
	}
	
	public void setUpDownJoystickID(int up, int down) {
		stickUpID 			= up;
		stickDownID 		= down;
		upDownEnabled	 	= true;
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
			
				// Check to see if update to server is necessary since the interval between
				// -Sensitivity < CurrPosX < Sensitivity should only update if keypressing is active
				if(indicateKeyPress && (currPosX > -SENSITIVITY && currPosX < SENSITIVITY)) {
					indicateKeyPress = false;
					if(currPosX >= 0)
						notifyComm(new CommunicationNotifier(stickRightID, currPosX));
					else
						notifyComm(new CommunicationNotifier(stickLeftID, currPosX));
				}
				else if(!(currPosX > -SENSITIVITY && currPosX < SENSITIVITY)){
					indicateKeyPress = true;
					if(currPosX >= 0)
						notifyComm(new CommunicationNotifier(stickRightID, currPosX));
					else
						notifyComm(new CommunicationNotifier(stickLeftID, currPosX));							
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
			
				// Check to see if update to server is necessary since the interval between
				// -Sensitivity < CurrPosY < Sensitivity should only update if keypressing is active
				if(indicateKeyPress && (currPosY > -SENSITIVITY && currPosY < SENSITIVITY)) {
					indicateKeyPress = false;
					if(currPosY >= 0)
						notifyComm(new CommunicationNotifier(stickUpID, currPosY));
					else
						notifyComm(new CommunicationNotifier(stickDownID, currPosY));
				}
				else if(!(currPosY > -SENSITIVITY && currPosY < SENSITIVITY)){
					indicateKeyPress = true;
					if(currPosY >= 0)
						notifyComm(new CommunicationNotifier(stickUpID, currPosY));
					else
						notifyComm(new CommunicationNotifier(stickDownID, currPosY));							
				}
			}
		}
	}
	
	public boolean isIndicateKeyPress() {
		return indicateKeyPress;
	}



	private void notifyComm(CommunicationNotifier notifier) {
		setChanged();
		notifyObservers(notifier);
	}
	

}
