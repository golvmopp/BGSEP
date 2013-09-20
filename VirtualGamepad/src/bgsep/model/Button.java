package bgsep.model;

import java.util.Observable;
import java.util.Observer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class Button extends Observable {
	
	/* **** Button Identifiers **** */
	public static final int BUTTON_0 = 0;
	public static final int BUTTON_1 = 1;
	public static final int BUTTON_A = 2;
	public static final int BUTTON_B = 3;
	/* **************************** */

	private int buttonIdentifier;
	private boolean isPressed;
	
	public Button(ImageView button, int identifier, Observer obs) {
		buttonIdentifier = identifier;
		button.setOnTouchListener(new ButtonTouchEvent());
		addObserver(obs);
		isPressed = false;
	}
	
	private class ButtonTouchEvent implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPressed = true;
				
				//Notify observers
				setChanged();
				notifyObservers(buttonIdentifier);
				break;
			case MotionEvent.ACTION_UP:
				isPressed = false;
				
				//Notify observers
				setChanged();
				notifyObservers(buttonIdentifier);
				break;
			default:
				break;
			}
			return true;
		}
		
	}
	
	public boolean isPressed() {
		return isPressed;
	}

}
