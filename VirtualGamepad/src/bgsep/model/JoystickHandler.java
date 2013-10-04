package bgsep.model;

import java.nio.ByteBuffer;

import android.widget.ImageView;

/**
 * Subclass of Joystick that handles joystick movements.
 * @author patrik
 *
 */

public class JoystickHandler extends Joystick {

	private boolean upDownEnabled, leftRightEnabled;
	private int[]	upDownKeyCode, leftRightKeyCode;
	private int		upDownIndication, leftRightIndication;
	private float	prevPosX, prevPosY;
	
	private final int 	LEFT_INDICATION 	= 0,
						RIGHT_INDICATION 	= 1,
						UP_INDICATION 		= 0,
						DOWN_INDICATION 	= 1;
	
	/**
	 * Takes a positioned boundary ImageView and a positioned
	 * stick ImageView. Enabled by default.
	 * @param boundary an ImageView
	 */
	public JoystickHandler(ImageView boundary, ImageView stick) {
		super(boundary, stick);
		upDownEnabled = leftRightEnabled = false;
		upDownKeyCode = new int[2];
		leftRightKeyCode = new int[2];
		upDownKeyCode[0] = upDownKeyCode[1] =
				leftRightKeyCode[0] = leftRightKeyCode [1] = 0;
		upDownIndication = leftRightIndication = 0;

		prevPosX = prevPosY = 0;
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
			ByteBuffer keyCodeBB = ByteBuffer.allocate(32);
			// Check to see if an update to server is necessary
			// according to the specified sensitivity
			if((getX() >= 0.5 && prevPosX >= 0.5) || 
					(getX() <= -0.5 && prevPosX <= -0.5) ||
					((getX() < 0.5 && getX() > -0.5) && 
							(prevPosX < 0.5 && prevPosX > -0.5)))
				prevPosX = getX();
			else { // Update necessary
			
				// Determine if the joystick indicates left or right
				if(getX() < 0) // Indicate left
					leftRightIndication = LEFT_INDICATION;
				else if(getX() > 0) // Indicate right
					leftRightIndication = RIGHT_INDICATION;
			
				keyCodeBB.putInt(leftRightKeyCode[leftRightIndication]);
			
				// Set first bit to 1 to indicate a keyPress, else its 0 by default
				// and indicates a keyRelease
				if(getX() >= 0.5 || getX() <= -0.5)
					keyCodeBB.array()[0] = 1;
			
				prevPosX = getX();
			
				// Update the server
				setChanged();
				notifyObservers(keyCodeBB);
			}
		}
		
		if(upDownEnabled) {
			ByteBuffer keyCodeBB = ByteBuffer.allocate(32);
			// Check to see if an update to server is necessary
			// according to the specified sensitivity
			if((getY() >= 0.5 && prevPosY >= 0.5) || 
					(getY() <= -0.5 && prevPosY <= -0.5) ||
					((getY() < 0.5 && getY() > -0.5) && 
							(prevPosY < 0.5 && prevPosY > -0.5)))
				prevPosY = getY();
			else { // Update necessary
				
				// Determine if the joystick indicates up or down
				if(getY() < 0) // Indicate down
					upDownIndication = DOWN_INDICATION;
				else if(getY() > 0) // Indicate up
					upDownIndication = UP_INDICATION;
			
				keyCodeBB.putInt(upDownKeyCode[upDownIndication]);
			
				// Set first bit to 1 to indicate a keyPress, else its 0 by default
				// and indicates a keyRelease
				if(getY() >= 0.5 || getY() <= -0.5)
					keyCodeBB.array()[0] = 1;
			
				prevPosY = getY();
			
				// Update the server
				setChanged();
				notifyObservers(keyCodeBB);
			}
		}
	}
	
	public void setLeftRightKeyCode(int leftKeyCode, int rightKeyCode) {
		leftRightKeyCode[LEFT_INDICATION] = leftKeyCode;
		leftRightKeyCode[RIGHT_INDICATION] = rightKeyCode;
		leftRightEnabled = true;
	}
	
	public void setUpDownKeyCode(int upKeyCode, int downKeyCode) {
		upDownKeyCode[UP_INDICATION] = upKeyCode;
		upDownKeyCode[DOWN_INDICATION] = downKeyCode;
		upDownEnabled = true;
	}

}
