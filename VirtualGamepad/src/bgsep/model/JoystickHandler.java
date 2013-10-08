package bgsep.model;

import android.drm.DrmStore.RightsStatus;
import android.widget.ImageView;
import bgsep.communication.CommunicationNotifier;

/**
 * Subclass of Joystick that handles joystick movements.
 * @author patrik
 *
 */

public class JoystickHandler extends Joystick {

	private int 	stickLeftID, stickRightID, stickUpID, stickDownID;
	private int		prevPosX, prevPosY;
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
			float currPosX = getX() * 10;
			
			int rounding = (int)(currPosX);
			if((rounding % 2) != 0) {
				rounding += currPosX > rounding ? 1 : -1; //wingardium leviosa
			}
			if(prevPosX != rounding) {
				prevPosX = rounding;
				if(currPosX > 0)
					notifyComm(new CommunicationNotifier(stickRightID, Math.abs(((float) rounding) / 10)));
				else if(currPosX < 0)
					notifyComm(new CommunicationNotifier(stickLeftID,  Math.abs(((float) rounding) / 10)));
				else
					notifyComm(new CommunicationNotifier((prevPosX > 0) ? stickRightID : stickLeftID , 0));
			}
		}
		
		if(upDownEnabled) {
			float currPosY = getY() * 10;
			
			int rounding = (int)(currPosY);
			if((rounding % 2) != 0) {
				rounding += currPosY > rounding ? 1 : -1; //wingardium leviosa
			}
			if(prevPosY != rounding) {
				prevPosY = rounding;
				if(currPosY > 0)
					notifyComm(new CommunicationNotifier(stickUpID, Math.abs(((float) rounding) / 10)));
				else if(currPosY < 0)
					notifyComm(new CommunicationNotifier(stickDownID,  Math.abs(((float) rounding) / 10)));
				else
					notifyComm(new CommunicationNotifier((prevPosY > 0) ? stickUpID : stickDownID , 0));
			}
		}
	}
	
	public boolean isIndicateKeyPress() {
		return indicateKeyPress;
	}

	private float round(float value) {
		int rounded = Math.round(value*10);
		return (float)rounded/10;
	}


	private void notifyComm(CommunicationNotifier notifier) {
		setChanged();
		notifyObservers(notifier);
	}
	

}
