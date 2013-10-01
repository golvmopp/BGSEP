package bgsep.virtualgamepad;

import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import bgsep.model.JoystickHandler;
import bgsep.model.KeyCode;
import android.app.Activity;
import android.os.Bundle;
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
}
