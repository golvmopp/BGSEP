package bluetooth;

import java.util.HashMap;

import bgsep.virtualgamepad.MainActivity;
import bgsep.virtualgamepad.R;
import android.R.layout;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager.RegistrationListener;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class BluetoothHandler {
	
	private HashMap<String, String> deviceMap;
	private Activity activity;
	private static final String TAG = "Gamepad";
	private TextView text;
	
	public BluetoothHandler(Activity activity) {
		text = (TextView)activity.findViewById(R.id.log);
		deviceMap = new HashMap<String, String>();
		this.activity = activity;
		BluetoothAdapter ba = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
		if (ba == null) {
			log("No bluetooth adapter detected!");
			return;
		} else {
			log("Bluetooth adapter \"" + ba.getName() + "\" detected");
		}
		if (ba.isEnabled()) {
			log("Bluetooth device is enabled");
		} else {
			log("Bluetooth device is disabled");
			log("Enabling bluetooth device..");
			log(ba.enable() ? "Success" : "Failed");
		}
		log("Starting discovery...");
		if (ba.startDiscovery()) {
			log("Discovering...");
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			activity.registerReceiver(mReciever, filter);
		} else {
			log("Discovering did not start?");
		}
		log("Discovery done");
		
	}
	
	private void log(String text) {
		this.text.setText(this.text.getText() + "\n" + text);
	}
	
	private final BroadcastReceiver mReciever = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				deviceMap.put(device.getName(), device.getAddress());
			}	
		}
	};
}
