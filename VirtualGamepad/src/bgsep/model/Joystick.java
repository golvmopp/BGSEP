package bgsep.model;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


/**
 * Handles joystick movements.
 * @author patrik
 *
 */
public class Joystick {

	private ImageView boundary;
	private ImageView stick;
	private float prevPosX;
	private float prevPosY;
	private float startPointX;
	private float startPointY;
	private boolean enabled;
	
	/**
	 * Takes a positioned boundary ImageView and stick ImageView. Enabled by default.
	 * @param boundary an ImageView
	 * @param stick an ImageView
	 */
	public Joystick(ImageView boundary, ImageView stick)
	{
		prevPosX		= 0;
		prevPosY 		= 0;
		startPointX 	= stick.getX();
		startPointY 	= stick.getY();
		boundary.setOnTouchListener(new JoystickTouchEvent());
		
		this.boundary 	= boundary;
		this.stick 		= stick;
		
		enabled = true;
	}
	
	private class JoystickTouchEvent implements OnTouchListener
	{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if(enabled)
			{
				switch(event.getAction())
				{
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
					stick.setX(startPointX);
					stick.setY(startPointY);
				}
				return true;
			}			
			return false;
		}
		
		private void moveJoystick(float XPos, float YPos)
		{
			stick.setX(boundary.getLeft() + XPos - stick.getWidth()/2);
			stick.setY(boundary.getTop() + YPos - stick.getHeight()/2);
		
			//Update previous position
			prevPosX = XPos;
			prevPosY = YPos;
		}
		
	}
	
	public boolean getEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
}
