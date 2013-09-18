package bgsep.model;

import java.util.Observable;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class Button extends Observable {
	
	static final int BUTTON_0 = 0;
	static final int BUTTON_1 = 1;
	

	private int buttonIdentifier;
	
	public Button(ImageView button, int identifier) {
		buttonIdentifier = identifier;
		button.setOnTouchListener(new ButtonTouchEvent());
	}
	
	private class ButtonTouchEvent implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			setChanged();
			notifyObservers(buttonIdentifier);
			return true;
		}
		
	}	

}
