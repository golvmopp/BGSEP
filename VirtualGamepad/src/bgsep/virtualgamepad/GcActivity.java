package bgsep.virtualgamepad;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import bgsep.model.JoystickHandler;
import bgsep.model.KeyCode;
import bgsep.wifi.Client;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class GcActivity extends Activity implements Observer {

	private boolean isInitialized;
	private JoystickHandler gcJoystick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gc);
		
		isInitialized = false;
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasChanged) {
		// Initialization of the joystick must happen when all the views has been drawn.
		// Therefor initialize it when the window has focus and not in onCreate.
		if(!isInitialized) {
			ImageView boundary  = (ImageView)findViewById(R.id.gc_joystickboundary);
			ImageView stick 	= (ImageView)findViewById(R.id.gc_joystick);
			
			gcJoystick = new JoystickHandler(boundary, stick);
			gcJoystick.setLeftRightKeyCode(KeyCode.VK_LEFT, KeyCode.VK_RIGHT);
			gcJoystick.setUpDownKeyCode(KeyCode.VK_UP, KeyCode.VK_DOWN);
			gcJoystick.addObserver(this);
			
			Client networkClient = new Client();
			gcJoystick.addObserver(networkClient);
			
			isInitialized = true;
		}
	}



	@Override
	public void update(Observable o, Object data) {
		// Joystick movement handling
		if(o instanceof JoystickHandler && !(data instanceof ByteBuffer)) {
			JoystickHandler joystick = (JoystickHandler)o;
			ImageView stick = joystick.getStick();
			ImageView boundary = joystick.getBoundary();
			
			stick.setX((joystick.getX() * boundary.getWidth()/2) + boundary.getLeft() + boundary.getWidth()/2-stick.getWidth()/2);
			stick.setY((joystick.getY() * boundary.getHeight()/2 * -1) + boundary.getTop() + boundary.getHeight()/2-stick.getHeight()/2);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	        
	        case R.id.action_ps:
	        	i = new Intent(this, PsActivity.class);
	    		startActivity(i);
	            finish();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
