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
import bluetooth.BluetoothHandler;
import bluetooth.SenderImpl;
//import bluetooth.BluetoothHandler;

public class MainActivity extends Activity implements Observer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		ImageView 		imageNESbutton, imageGCbutton, imagePSbutton;
		imageNESbutton = (ImageView)findViewById(R.id.mainpage_nes);
		imageGCbutton = (ImageView)findViewById(R.id.mainpage_gc);
		imagePSbutton = (ImageView)findViewById(R.id.mainpage_ps);
		new Button(imageNESbutton, R.drawable.mainpage_nes, R.drawable.mainpage_nes_pr,
				45, this);
		new Button(imageGCbutton, R.drawable.mainpage_gc, R.drawable.mainpage_gc_pr,
				46, this);
		new Button(imagePSbutton, R.drawable.mainpage_ps, R.drawable.mainpage_ps_pr,
				47, this);
		
		
		//Hides soft menu keys if present
		if (!ViewConfiguration.get(this).hasPermanentMenuKey())
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);	
		/*
		refer to:
		http://stackoverflow.com/questions/937313/android-basic-gesture-detection
		for how to recognize swipe
		*/


		/*BluetoothHandler bh = null;
		if(android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
			bh = new BluetoothHandler(this);*/
		
		BluetoothHandler bh = new BluetoothHandler(this);
		bh.start();

		SenderImpl si = new SenderImpl(bh);
		Communication communication = Communication.getInstance();
		communication.setSender(si);
		//Intent i = new Intent(this, GcActivity.class);
		//startActivity(i);

	}

	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Button) {
			Button button = (Button)o;
			Intent i;
			if(button.isPressed())
				button.getButtonView().setImageResource(button.getPressedDrawableID());
			
			else {
				switch(button.getButtonID()) {
				case 45:
					i = new Intent(this, NesActivity.class);
					startActivity(i);
					break;
				case 46:
					i = new Intent(this, GcActivity.class);
					startActivity(i);
					break;
				case 47:
					/*i = new Intent(this, NesActivity.class);
					startActivity(i);*/
					break;
				default:
					break;
				}
				button.getButtonView().setImageResource(button.getUnPressedDrawableID());
			}
				
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
	            return true;
	            
	        case R.id.action_gc:
	        	i = new Intent(this, GcActivity.class);
	    		startActivity(i);
	            return true;
	        
	        case R.id.action_ps:
	        	i = new Intent(this, PsActivity.class);
	    		startActivity(i);
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	
}
