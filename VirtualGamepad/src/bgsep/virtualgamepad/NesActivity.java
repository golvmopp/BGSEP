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
import bgsep.model.Button;

public class NesActivity extends Activity implements Observer {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nes);
		
		if (!ViewConfiguration.get(this).hasPermanentMenuKey())
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);	

		
		ImageView 		imageAbutton, 	imageBbutton,
						imageLeftArrow, imageRightArrow,
						imageUpArrow, 	imageDownArrow,
						imageSelect,	imageStart;
		
		Button			aButton, bButton, leftArrowButton,
						rightArrowButton, upArrowButton, downArrowButton,
						selectButton, startButton;;
		
		imageAbutton 	= (ImageView) findViewById(R.id.nes_a_button);
		imageBbutton 	= (ImageView) findViewById(R.id.nes_b_button);
		imageLeftArrow 	= (ImageView) findViewById(R.id.nes_left_arrow);
		imageRightArrow = (ImageView) findViewById(R.id.nes_right_arrow);
		imageUpArrow 	= (ImageView) findViewById(R.id.nes_up_arrow);
		imageDownArrow 	= (ImageView) findViewById(R.id.nes_down_arrow);
		imageSelect		= (ImageView) findViewById(R.id.nes_select_button);
		imageStart		= (ImageView) findViewById(R.id.nes_start_button);
		
		aButton = new Button(imageAbutton, R.drawable.nes_a_button, R.drawable.nes_a_button_pressed,
				0, this);
		
		bButton = new Button(imageBbutton, R.drawable.nes_b_button, R.drawable.nes_b_button_pressed,
				1, this);
		
		leftArrowButton = new Button(imageLeftArrow, 2);
		
		rightArrowButton = new Button(imageRightArrow, 3);
		
		upArrowButton = new Button(imageUpArrow, 4);
		
		downArrowButton = new Button(imageDownArrow, 5);
		
		selectButton = new Button(imageSelect, 15);
		
		startButton = new Button(imageStart, 16);
		
		Communication comm = Communication.getInstance();
		
		aButton.addObserver(comm);
		bButton.addObserver(comm);
		leftArrowButton.addObserver(comm);
		rightArrowButton.addObserver(comm);
		upArrowButton.addObserver(comm);
		downArrowButton.addObserver(comm);
		selectButton.addObserver(comm);
		startButton.addObserver(comm);
		
		
		
	}

	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Button) {
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
