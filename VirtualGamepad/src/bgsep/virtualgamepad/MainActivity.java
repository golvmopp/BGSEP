package bgsep.virtualgamepad;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import bgsep.communication.Communication;
import bgsep.model.Button;
import bluetooth.BluetoothHandler;
import bluetooth.SenderImpl;

public class MainActivity extends Activity implements Observer {

	private BluetoothHandler bh;
	private ImageView communicationIndicator, communicationButton, connectText;
	private Animation rotate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		ImageView 		imageNESbutton, imageGCbutton, imagePSbutton;
		
		imageNESbutton = (ImageView)findViewById(R.id.mainpage_nes);
		imageGCbutton = (ImageView)findViewById(R.id.mainpage_gc);
		imagePSbutton = (ImageView)findViewById(R.id.mainpage_ps);
		communicationButton = (ImageView) findViewById(R.id.mainpage_connection_button);
		communicationIndicator = (ImageView)findViewById(R.id.mainpage_connection_indicator);
		connectText = (ImageView) findViewById(R.id.mainpage_connect_text);
		
		communicationIndicator.setVisibility(View.INVISIBLE);
		rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_view);
		
		new Button(imageNESbutton, R.drawable.mainpage_nes, R.drawable.mainpage_nes_pr,
				45, this);
		new Button(imageGCbutton, R.drawable.mainpage_gc, R.drawable.mainpage_gc_pr,
				46, this);
		new Button(imagePSbutton, R.drawable.mainpage_ps, R.drawable.mainpage_ps_pr,
				47, this);
		
		bh = new BluetoothHandler(this);
		communicationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bh.isConnected()) {
					bh.disconnect(true);
				} else {
					if (!bh.isStarted()) {
						Log.d("Gamepad", "BluetoothHandler is not alive, starting it..");
						indicateConnecting();
						bh.startThread();
						
					} else {
						Log.d("Gamepad", "disconnected from server but is alive");
					}
				}
			}
		});

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
					i = new Intent(this, PsActivity.class);
					startActivity(i);
					break;
				default:
					break;
				}
				button.getButtonView().setImageResource(button.getUnPressedDrawableID());
			}
				
		}
	}
	
	public void serverDisconnected() {
		Log.d("Gamepad", "BLI RÖD!");
		if(communicationIndicator.getVisibility() == View.VISIBLE) {
			communicationIndicator.setAnimation(null);
			communicationIndicator.setVisibility(View.INVISIBLE);
		}
		
		communicationButton.setImageResource(R.drawable.mainpage_red_arrows);
		connectText.setVisibility(View.VISIBLE);
	}
	
	public void serverConnected() {
		Log.d("Gamepad", "BLI GRÖN!");
		communicationIndicator.setAnimation(null);
		communicationIndicator.setVisibility(View.INVISIBLE);
		communicationButton.setImageResource(R.drawable.mainpage_green_arrows);
		
	}
	
	private void indicateConnecting() {
		connectText.setVisibility(View.INVISIBLE);
		communicationButton.setImageResource(R.drawable.mainpage_connect_button);
		communicationIndicator.setVisibility(View.VISIBLE);
		communicationIndicator.startAnimation(rotate);
	}
}
