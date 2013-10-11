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
import java.util.Observer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * description...
 * 
 * @author
 * 
 */
public class Button extends Observable {
	private int buttonID;
	private ImageView buttonView;
	private boolean isPressed;
	private int pressedDrawableID, unPressedDrawableID;

	public Button(ImageView button, int unPressedDrawableID, int pressedDrawableID, int id, Observer o) {
		this(button, unPressedDrawableID, pressedDrawableID);
		buttonID = id;
		addObserver(o);
	}

	/**
	 * If the button does not have a "pressed look". This type of button should
	 * not be observed by observers that changes its drawables.
	 * 
	 * @param button
	 * @param id
	 */
	public Button(ImageView button, int id) {
		this(button, 0, 0);
		buttonID = id;
	}

	/**
	 * If the button doesnt need an ID for server communication. This type of
	 * Button should now have a communication observer
	 */
	public Button(ImageView button, int unPressedDrawableID, int pressedDrawableID) {
		buttonID = 0;
		button.setOnTouchListener(new ButtonTouchEvent());
		this.pressedDrawableID = pressedDrawableID;
		this.unPressedDrawableID = unPressedDrawableID;
		isPressed = false;
		buttonView = button;
	}

	public void setButtonID(int id) {
		buttonID = id;
	}

	public int getButtonID() {
		return buttonID;
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

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPressed = true;
				setChanged();
				notifyObservers();
				break;
			case MotionEvent.ACTION_UP:
				isPressed = false;
				setChanged();
				notifyObservers();
				break;
			default:
				break;
			}

			return true;
		}

	}

	public int getPressedDrawableID() {
		return pressedDrawableID;
	}

	public int getUnPressedDrawableID() {
		return unPressedDrawableID;
	}
}