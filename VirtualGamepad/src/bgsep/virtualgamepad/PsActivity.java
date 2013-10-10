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
import bgsep.model.Button;

public class PsActivity extends Activity implements Observer {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ps);
		
		//Dim soft menu keys if present
		if (!ViewConfiguration.get(this).hasPermanentMenuKey())
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
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
			getMenuInflater().inflate(R.menu.ps, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
			Intent i;
		    switch (item.getItemId()) {
	        
		        case R.id.action_nes:
		        	i = new Intent(this, GcActivity.class);
		    		startActivity(i);
		            finish();
		            return true;
		        
		        case R.id.action_gc:
		        	i = new Intent(this, PsActivity.class);
		    		startActivity(i);
		            finish();
		            return true;
		            
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
}
