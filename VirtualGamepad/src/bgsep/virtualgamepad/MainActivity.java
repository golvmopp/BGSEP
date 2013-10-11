/* Copyright (C) <2013>  <Victor Olausson, Patrik Wållgren>

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

/**
 * This is the starting activity to allow a user to choose a gamepad,
 * get information/help about the app, and (dis)connect to the server  
 * @author Patrik Wållgren
 * @author Victor Olausson
 *
 */
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
					startBluetooth();
				}
			}
		});

		SenderImpl si = new SenderImpl(bh);
		Communication communication = Communication.getInstance();
		communication.setSender(si);
	}
	
	private void startBluetooth() {
		if (!bh.isStarted()) {
			Log.d("Gamepad", "BluetoothHandler is not alive, starting it..");
			indicateConnecting();
			bh.startThread();
		} else {
			Log.d("Gamepad", "disconnected from server but is alive");
		}
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
	
	/**
	 * Indicate to GUI that the server is not connected. 
	 */
	public void serverDisconnected() {
		if(communicationIndicator.getVisibility() == View.VISIBLE) {
			communicationIndicator.setAnimation(null);
			communicationIndicator.setVisibility(View.INVISIBLE);
		}
		communicationButton.setImageResource(R.drawable.mainpage_red_arrows);
		connectText.setVisibility(View.VISIBLE);
		connectText.setImageResource(R.drawable.mainpage_connect_text);
	}
	
	/**
	 * Indicate to GUI that the server is connected.
	 */
	public void serverConnected() {
		connectText.setVisibility(View.VISIBLE);
		connectText.setImageResource(R.drawable.mainpage_disconnect_text);
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
	
	@Override
	public void onWindowFocusChanged(boolean has) {
		startBluetooth();
	}
}
