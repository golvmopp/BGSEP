package bgsep.virtualgamepad;

import bgsep.communication.Communication;
import bluetooth.BluetoothHandler;
import bluetooth.SenderImpl;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		BluetoothHandler bh = new BluetoothHandler(this);
		bh.start();
		SenderImpl si = new SenderImpl(bh);
		Communication communication = Communication.getInstance();
		communication.setSender(si);
		Intent i = new Intent(this, GcActivity.class);
		startActivity(i);
		//finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
