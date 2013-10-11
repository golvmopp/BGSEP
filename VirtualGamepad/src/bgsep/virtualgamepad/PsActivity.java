/* Copyright (C) 2013  Victor Olausson, Patrik Wållgren

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/  */

package bgsep.virtualgamepad;

import java.util.Observable;
import java.util.Observer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import bgsep.communication.Communication;
import bgsep.communication.CommunicationNotifier;
import bgsep.model.Button;
import bgsep.model.JoystickHandler;

/**
 * The activity for the PS controller
 * @author Victor Olausson
 * @author Patrik Wållgren
 *
 */
public class PsActivity extends Activity implements Observer {

	private ImageView 	imageX, imageCircle, imageSquare, imageTriangle,
						imageUp, imageDown, imageRight, imageLeft,
						imageR1, imageR2, imageL1, imageL2,
						imageStart, imageSelect,
						imageLeftStick, imageLeftBoundary, imageRightStick, imageRightBoundary;

	private Button		buttonX, buttonCircle, buttonSquare, buttonTriangle,
						buttonUp, buttonDown, buttonRight, buttonLeft,
						buttonR1, buttonR2, buttonL1, buttonL2,
						buttonStart, buttonSelect;

	JoystickHandler leftJoystick, rightJoystick;	
	
	private boolean isInitialized;
	
	Communication comm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ps);
		
		//Dim soft menu keys if present
		if (!ViewConfiguration.get(this).hasPermanentMenuKey())
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		comm = Communication.getInstance();
		isInitialized = false;
	}
		
		
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		// Initialization of the joystick must happen when all the views has been drawn.
		// Therefore initialize it when the window has focus and not in onCreate.
		if(!isInitialized) {
			initImages();
			initButtons();
			initJoysticks();
			isInitialized = true;
			
		}
		super.onWindowFocusChanged(hasFocus);
	}


	private void initImages() {
		imageX = (ImageView) findViewById(R.id.ps_x);
		imageCircle = (ImageView) findViewById(R.id.ps_o);
		imageSquare = (ImageView) findViewById(R.id.ps_sq);
		imageTriangle = (ImageView) findViewById(R.id.ps_tr);
		imageUp = (ImageView) findViewById(R.id.ps_arrow_up);
		imageDown = (ImageView) findViewById(R.id.ps_arrow_down);
		imageLeft = (ImageView) findViewById(R.id.ps_arrow_left);
		imageRight = (ImageView) findViewById(R.id.ps_arrow_right);
		imageR1 = (ImageView) findViewById(R.id.ps_r1);
		imageR2 = (ImageView) findViewById(R.id.ps_r2);
		imageL1 = (ImageView) findViewById(R.id.ps_l1);
		imageL2 = (ImageView) findViewById(R.id.ps_l2);
		imageStart = (ImageView) findViewById(R.id.ps_start);
		imageSelect = (ImageView) findViewById(R.id.ps_select);
		imageLeftBoundary = (ImageView) findViewById(R.id.ps_joy_bg_left);
		imageRightBoundary = (ImageView) findViewById(R.id.ps_joy_bg_right);
		imageLeftStick = (ImageView) findViewById(R.id.ps_joy_left);
		imageRightStick = (ImageView) findViewById(R.id.ps_joy_right);
	}
	
	private void initButtons() {
		buttonLeft = new Button(imageLeft, R.drawable.ps_arrow_left, R.drawable.ps_arrow_left_pr,
				0, this);
		buttonRight = new Button(imageRight, R.drawable.ps_arrow_right, R.drawable.ps_arrow_right_pr,
				1, this);
		buttonUp = new Button(imageUp, R.drawable.ps_arrow_up, R.drawable.ps_arrow_up_pr,
				2, this);
		buttonDown = new Button(imageDown, R.drawable.ps_arrow_down, R.drawable.ps_arrow_down_pr,
				3, this);
		buttonX = new Button(imageX, R.drawable.ps_x, R.drawable.ps_x_pr,
				4, this);
		buttonCircle = new Button(imageCircle, R.drawable.ps_o, R.drawable.ps_o_pr,
				5, this);
		buttonTriangle = new Button(imageTriangle, R.drawable.ps_tr, R.drawable.ps_tr_pr,
				6, this);
		buttonSquare = new Button(imageSquare, R.drawable.ps_sq, R.drawable.ps_sq_pr,
				7, this);
		buttonR1 = new Button(imageR1, R.drawable.ps_r1, R.drawable.ps_r1_pr,
				8, this);
		buttonR2 = new Button(imageR2, R.drawable.ps_r2, R.drawable.ps_r2_pr,
				9, this);
		buttonL1 = new Button(imageL1, R.drawable.ps_l1, R.drawable.ps_l1_pr,
				10, this);
		buttonL2 = new Button(imageL2, R.drawable.ps_l2, R.drawable.ps_l2_pr,
				11, this);
		buttonSelect = new Button(imageSelect, R.drawable.ps_select, R.drawable.ps_select_pr,
				12, this);
		buttonStart = new Button(imageStart, R.drawable.ps_start, R.drawable.ps_start_pr,
				13, this);
		
		buttonLeft.addObserver(comm);
		buttonRight.addObserver(comm);
		buttonUp.addObserver(comm);
		buttonDown.addObserver(comm);
		buttonX.addObserver(comm);
		buttonCircle.addObserver(comm);
		buttonTriangle.addObserver(comm);
		buttonSquare.addObserver(comm);
		buttonR1.addObserver(comm);
		buttonR2.addObserver(comm);
		buttonL1.addObserver(comm);
		buttonL2.addObserver(comm);
		buttonSelect.addObserver(comm);
		buttonStart.addObserver(comm);
		
		
	}
	
	private void initJoysticks() {
		leftJoystick = new JoystickHandler(imageLeftBoundary, imageLeftStick);
		rightJoystick = new JoystickHandler(imageRightBoundary, imageRightStick);
		
		leftJoystick.setLeftRightJoystickID(14, 15);
		leftJoystick.setUpDownJoystickID(16, 17);
		rightJoystick.setLeftRightJoystickID(18, 19);
		rightJoystick.setUpDownJoystickID(20, 21);
		
		leftJoystick.addObserver(this);
		leftJoystick.addObserver(comm);
		rightJoystick.addObserver(this);
		rightJoystick.addObserver(comm);
	}
	
	@Override
	public void update(Observable o, Object data) {
		// Joystick and Button movement handling
		
		if(o instanceof JoystickHandler && !(data instanceof CommunicationNotifier)) {
			JoystickHandler joystick = (JoystickHandler)o;
			ImageView stick = joystick.getStick();
			ImageView boundary = joystick.getBoundary();
					
			stick.setX((joystick.getX() * boundary.getWidth()/2) + boundary.getLeft() + boundary.getWidth()/2-stick.getWidth()/2);
			stick.setY((joystick.getY() * boundary.getHeight()/2 * -1) + boundary.getTop() + boundary.getHeight()/2-stick.getHeight()/2);
		}
		else if(o instanceof Button) {
			Button button = (Button)o;
			
			if(button.isPressed())
				button.getButtonView().setImageResource(button.getPressedDrawableID());
			else
				button.getButtonView().setImageResource(button.getUnPressedDrawableID());
				
		}
	}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.ps, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
			Intent i;
		    switch (item.getItemId()) {
	        
		        case R.id.action_nes:
		        	i = new Intent(this, NesActivity.class);
		    		startActivity(i);
		            finish();
		            return true;
		        
		        case R.id.action_gc:
		        	i = new Intent(this, GcActivity.class);
		    		startActivity(i);
		            finish();
		            return true;
		            
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
}
