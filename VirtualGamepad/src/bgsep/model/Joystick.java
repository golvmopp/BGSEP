/*
   Copyright (C) 2013  Patrik Wållgren

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

package bgsep.model;
import java.util.Observable;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


/**
 * A superclass that handles joystick movements.
 * You should implement a subclass that overrides
 * onStickMovement()
 * @author Patrik
 *
 */
public abstract class Joystick extends Observable {

	private float 		prevPosX, prevPosY;
	private float 		startPosX, startPosY;
	private boolean 	enabled;
	private ImageView 	boundary, stick;
	
	/**
	 * Takes a positioned boundary ImageView and a positioned
	 * stick ImageView. Enabled by default.
	 * @param boundary an ImageView
	 */
	public Joystick(ImageView boundary, ImageView stick) {
		prevPosX = prevPosY = 0;
		startPosX 	= boundary.getWidth()/2;
		startPosY 	= boundary.getHeight()/2;

		boundary.setOnTouchListener(new JoystickTouchEvent());
		
		this.boundary 	= boundary;
		this.stick = stick;
		
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
					prevPosX = startPosX;
					prevPosY = startPosY;
					onStickMovement();
					break;
					
				default:
					break;
				}
				return true;
			}			
			return false;
		}
		
		private void moveJoystick(float XPos, float YPos) {
			//Update previous position
			prevPosX = XPos;
			prevPosY = YPos;
			
			onStickMovement();
		}
		
	}
	
	/**
	 * Returns the X position of the stick where origo is the middle point in the
	 * boundary
	 * @return the X position of the stick
	 */
	public float getX() {
		return (prevPosX - startPosX) / (boundary.getWidth()/2);
	}
	
	/**
	 * Returns the Y position of the stick where origo is the middle point in the
	 * boundary
	 * @return the Y position of the stick
	 */
	public float getY() {
		return (prevPosY - startPosY) / (boundary.getHeight()/2) * -1;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public ImageView getBoundary() {
		return boundary;
	}

	public ImageView getStick() {
		return stick;
	}

	/**
	 * Method to be overriden by subclasses when the user moves the stick
	 */
	abstract public void onStickMovement();
}
