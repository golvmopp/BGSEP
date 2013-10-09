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
import android.widget.Toast;
import bgsep.communication.Communication;
import bgsep.model.Button;
import bluetooth.BluetoothHandler;
import bluetooth.SenderImpl;

public class MainActivity extends Activity implements Observer {

	private BluetoothHandler bh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		ImageView 		imageNESbutton, imageGCbutton, imagePSbutton,
						imageCommButton;
		Button			nesButton, gcButton, psButton;
		
		imageNESbutton = (ImageView)findViewById(R.id.mainpage_nes);
		imageGCbutton = (ImageView)findViewById(R.id.mainpage_gc);
		imagePSbutton = (ImageView)findViewById(R.id.mainpage_ps);
		
		nesButton = new Button(imageNESbutton, R.drawable.mainpage_nes, R.drawable.mainpage_nes_pr);
		gcButton = new Button(imageGCbutton, R.drawable.mainpage_gc, R.drawable.mainpage_gc_pr);
		psButton = new Button(imagePSbutton, R.drawable.mainpage_ps, R.drawable.mainpage_ps_pr);
		
		nesButton.addObserver(this);
		gcButton.addObserver(this);
		psButton.addObserver(this);
		
		
		bh = new BluetoothHandler(this);
		bh.start();

		SenderImpl si = new SenderImpl(bh);
		Communication communication = Communication.getInstance();
		communication.setSender(si);
		
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
	
	public void serverDisconnected() {
		
	}
}
