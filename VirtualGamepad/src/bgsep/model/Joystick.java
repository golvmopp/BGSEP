package bgsep.model;
import java.util.Observable;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


/**
 * Handles joystick movements.
 * @author patrik
 *
 */
public class Joystick extends Observable {

	//private ImageView boundary;
	//private ImageView stick;
	private float 	prevPosX, prevPosY;
	private float 	currPosX, currPosY;
	private float 	startPosX;
	private float 	startPosY;
	private boolean enabled;
	ImageView 		boundary, stick;
	
	/**
	 * Takes a positioned boundary ImageView and stick ImageView. Enabled by default.
	 * @param boundary a Corners
	 * @param stick a Corners
	 */
	public Joystick(ImageView boundary, ImageView stick) {
		prevPosX = prevPosY = currPosX = currPosY = 0;
		startPosX 	= stick.getX();
		startPosY 	= stick.getY();
		boundary.setOnTouchListener(new JoystickTouchEvent());
		
		this.stick		= stick;
		this.boundary 	= boundary;
		
		enabled = true;
	}
	
	private class JoystickTouchEvent implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if(enabled) {
				switch(event.getAction()) {
				case MotionEvent.ACTION_MOVE:
				
					int maxTop, maxRight, maxBottom, maxLeft;
					maxTop = 0;
					maxRight = boundary.getWidth();
					maxLeft = 0;
					maxBottom = boundary.getHeight();
								
				
					if((event.getX() < maxLeft || event.getX() > maxRight) && 
							(event.getY() > maxTop && event.getY() < maxBottom))
						moveJoystick(prevPosX, event.getY());
					else if((event.getY() < maxTop || event.getY() > maxBottom) &&
							(event.getX() > maxLeft && event.getX() < maxRight))
						moveJoystick(event.getX(), prevPosY);
					else if(!(event.getX() < maxLeft || event.getX() > maxRight || 
								event.getY() < maxTop || event.getY() > maxBottom))
						moveJoystick(event.getX(), event.getY());
				
					break;
				
				case MotionEvent.ACTION_UP:
					//stick.setX(startPointX);
					//stick.setY(startPointY);
					currPosX = startPosX;
					currPosY = startPosY;
					setChanged();
					notifyObservers();
				}
				return true;
			}			
			return false;
		}
		
		private void moveJoystick(float XPos, float YPos) {
			currPosX = boundary.getLeft() + XPos - stick.getWidth()/2;
			currPosY = boundary.getTop() + YPos - stick.getHeight()/2;
			
			//Update previous position
			prevPosX = XPos;
			prevPosY = YPos;
			
			setChanged();
			notifyObservers();
		}
		
	}
	
	public float getX() {
		return currPosX;
	}
	
	public float getY() {
		return currPosY;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ImageView getStick() {
		return stick;
	}
	
}
