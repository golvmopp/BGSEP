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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import bgsep.communication.Communication;
import bgsep.communication.CommunicationNotifier;
import bgsep.model.Button;
import bgsep.model.Gyro;
import bgsep.model.JoystickHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

/**
 * The activity for the GC controller
 * @author Victor Olausson
 * @author Patrik Wållgren
 *
 */
public class GcActivity extends Activity implements Observer {

	private boolean isInitialized;
	ImageView aImageView, bImageView, xImageView, yImageView, imageStart,
			  imageBoundary, imageStick;
	private JoystickHandler gcJoystick;
	private Gyro gyro;
	private Button	aButton, bButton, xButton, yButton, startButton;
	private ArrayList<Button> buttons;
	
	private Communication comm;
	
	private boolean hapticFeedback;
	private boolean useAccelerometer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gc);
		
		// Keep screen on
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//Dim soft menu keys if present
		if (!ViewConfiguration.get(this).hasPermanentMenuKey())
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		Intent i = getIntent();
		hapticFeedback = i.getBooleanExtra("hapticFeedback", false);
		useAccelerometer = i.getBooleanExtra("useAccelerometer", false);
		
		isInitialized = false;
		comm = Communication.getInstance();
		
		if(useAccelerometer)
			initGyro();
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasChanged) {
		// Initialization of the joystick must happen when all the views has been drawn.
		// Therefore initialize it when the window has focus and not in onCreate.
		if(!isInitialized) {
			initImages();
			initButtons();
			initJoystick();
			isInitialized = true;
		}
		super.onWindowFocusChanged(hasChanged);
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
		getMenuInflater().inflate(R.menu.gc, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		Intent i;
	    switch (item.getItemId()) {
        
	        case R.id.action_nes:
	        	i = new Intent(this, NesActivity.class);
	        	i.putExtra("hapticFeedback", hapticFeedback);
	        	i.putExtra("useAccelerometer", useAccelerometer);
	    		startActivity(i);
	            finish();
	            return true;
	        
	        case R.id.action_ps:
	        	i = new Intent(this, PsActivity.class);
	        	i.putExtra("hapticFeedback", hapticFeedback);
	        	i.putExtra("useAccelerometer", useAccelerometer);
	    		startActivity(i);
	            finish();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void initImages() {
		aImageView = (ImageView)findViewById(R.id.gc_a_button);
		bImageView = (ImageView)findViewById(R.id.gc_b_button);
		xImageView = (ImageView)findViewById(R.id.gc_x_button);
		yImageView = (ImageView)findViewById(R.id.gc_y_button);
		imageStart = (ImageView)findViewById(R.id.gc_start_button);
		
		imageBoundary	= (ImageView)findViewById(R.id.gc_joystickboundary);
		imageStick 		= (ImageView)findViewById(R.id.gc_joystick);
	}
	
	private void initButtons() {
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		aButton = new Button(aImageView, R.drawable.gc_a_button, R.drawable.gc_a_button_pressed, 
				0, this, vibrator, hapticFeedback);
		bButton = new Button(bImageView, R.drawable.gc_b_button, R.drawable.gc_b_button_pressed, 
				1, this, vibrator, hapticFeedback);
		xButton = new Button(xImageView, R.drawable.gc_x_button, R.drawable.gc_x_button_pressed, 
				2, this, vibrator, hapticFeedback);
		yButton = new Button(yImageView, R.drawable.gc_y_button, R.drawable.gc_y_button_pressed, 
				3, this, vibrator, hapticFeedback);
		startButton = new Button(imageStart, R.drawable.gc_start_button, R.drawable.gc_start_button_pressed,
				4, this, vibrator, hapticFeedback);
		
		buttons = new ArrayList<Button>();
		
		buttons.add(aButton);
		buttons.add(bButton);
		buttons.add(startButton);
		buttons.add(xButton);
		buttons.add(yButton);
		
		for(Button b : buttons)
			b.addObserver(comm);
	}
	
	private void initJoystick() {
		gcJoystick = new JoystickHandler(imageBoundary, imageStick);
		gcJoystick.setLeftRightJoystickID(5, 6);
		gcJoystick.setUpDownJoystickID(7, 8);
		gcJoystick.addObserver(this);
		gcJoystick.addObserver(comm);
	}
	
	private void initGyro() {
		SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		gyro = new Gyro(sensorManager);
		gyro.setLeftRightGyroID(9, 10);
		gyro.setEnabled(useAccelerometer);
		gyro.registerListener();
		gyro.addObserver(comm);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseAliciaKeys();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		releaseAliciaKeys();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(useAccelerometer)
			gyro.registerListener();
	}
	
	private void releaseAliciaKeys() {
		if(useAccelerometer)
			gyro.unregisterListener();
		
		unPressAllButtons();
		gcJoystick.releaseJoystick();
	}
	
	private void unPressAllButtons() {
		for(Button b : buttons)
			b.setPressed(false);
	}
}
