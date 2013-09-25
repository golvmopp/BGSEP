package bgsep.model;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class Button extends Observable {

	private int buttonKeyCode;
	private ImageView buttonView;
	private boolean isPressed;
	private int pressedDrawableID, unPressedDrawableID;
	
	
	public Button(ImageView button, int unPressedDrawableID, int pressedDrawableID,
			int keyCode, Observer o) {
		this(button, unPressedDrawableID, pressedDrawableID);
		buttonKeyCode = keyCode;
		addObserver(o);
	}
	
	public Button(ImageView button, int unPressedDrawableID, int pressedDrawableID) {
		buttonKeyCode = 0;
		button.setOnTouchListener(new ButtonTouchEvent());
		this.pressedDrawableID = pressedDrawableID;
		this.unPressedDrawableID = unPressedDrawableID;
		isPressed = false;
		buttonView = button;
	}
	
	public void setButtonKeyCode(int keyCode) {
		buttonKeyCode = keyCode;
	}
	
	
	public ImageView getButtonView() {
		return buttonView;
	}

	public boolean isPressed() {
		return isPressed;
	}
	
	private class ButtonTouchEvent implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPressed = true;
				setChanged();
				notifyObservers(convertToByteBuffer(buttonKeyCode, isPressed));
				break;
			case MotionEvent.ACTION_UP:
				isPressed = false;
				setChanged();
				notifyObservers(convertToByteBuffer(buttonKeyCode, isPressed));
				break;
			default:
				break;	
			}
			
			return true;
		}
		
	}
	
	private ByteBuffer convertToByteBuffer(int value, boolean press) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		byteBuffer.putInt(value);
		
		// Set first bit to 1 to indicate a keyPress, else its 0 by default
		// and indicate a keyRelease
		if(press)
			byteBuffer.array()[0] = 1;
		
		return byteBuffer;
	}
	
	public int getPressedDrawableID() {
		return pressedDrawableID;
	}
	
	public int getUnPressedDrawableID() {
		return unPressedDrawableID;
	}

}
