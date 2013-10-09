package bgsep.model;

import android.util.Log;
import android.widget.ImageView;
import bgsep.communication.CommunicationNotifier;

/**
 * Subclass of Joystick that handles joystick movements.
 * @author patrik
 *
 */

public class JoystickHandler extends Joystick {

	private int stickLeftID, stickRightID, stickUpID, stickDownID;
	private int	prevPosX, prevPosY;
	private boolean leftRightEnabled, upDownEnabled;
	private boolean indicateKeyPress;
	
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
	 * Determines Left/Right/Up/Down and notifies the observers observers.
	 */
	@Override
	public void onStickMovement() {
		
		// Update views
		setChanged();
		notifyObservers();
		
		if(leftRightEnabled && upDownEnabled) { 
			// The axis with highest value got the highest priority
			if(getX() > getY()) {
				prevPosX = axisValueChanged(getX(), prevPosX, stickRightID, stickDownID);
				prevPosY = axisValueChanged(getY(), prevPosY, stickUpID, stickDownID);
			}
			else {
				prevPosY = axisValueChanged(getY(), prevPosY, stickUpID, stickDownID);
				prevPosX = axisValueChanged(getX(), prevPosX, stickRightID, stickDownID);
			}
				
		}
		else {
			if(leftRightEnabled)
				prevPosX = axisValueChanged(getX(), prevPosX, stickRightID, stickLeftID);
		
			if(upDownEnabled)
				prevPosY = axisValueChanged(getY(), prevPosY, stickUpID, stickDownID);
		}
	}
	
	private int axisValueChanged(float currPos, int prevPos, int stickIDpos, int stickIDneg) {
				
		int rounding = (int)(currPos*10);
		if((rounding % 2) != 0) {
			rounding += currPos > rounding ? 1 : -1;
		}
		
		if(prevPos != rounding) {
			float value = ((float)Math.abs(rounding))/10;
			if(rounding > 0) {
				notifyComm(new CommunicationNotifier(stickIDpos, value));
				notifyComm(new CommunicationNotifier(stickIDneg, 0));
			}
			else if(rounding < 0) {
				notifyComm(new CommunicationNotifier(stickIDneg,  value));
				notifyComm(new CommunicationNotifier(stickIDpos, 0));
			}	
			else {
				notifyComm(new CommunicationNotifier(stickIDpos, 0));
				notifyComm(new CommunicationNotifier(stickIDneg, 0));
			}
			return rounding;
		}
		return prevPos;
	}
	
	public boolean isIndicateKeyPress() {
		return indicateKeyPress;
	}


	private void notifyComm(CommunicationNotifier notifier) {
		setChanged();
		notifyObservers(notifier);
	}
	

}
