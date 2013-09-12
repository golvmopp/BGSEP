package bluetooth;

import java.util.HashMap;

import bgsep.virtualgamepad.MainActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdManager.RegistrationListener;

public class BluetoothHandler {
	
	private HashMap<String, String> deviceMap;
	private Activity activity;
	
	public BluetoothHandler(Activity activity) {
		deviceMap = new HashMap<String, String>();
		this.activity = activity;
		BluetoothAdapter ba = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
		if (ba == null) {
			System.out.println("No bluetooth adapter detected!");
		} else {
			System.out.println("Bluetooth adapter \"" + ba.getName() + "\" detected");
		}
		if (ba.isEnabled()) {
			System.out.println("Bluetooth device is enabled");
		} else {
			System.out.println("Bluetooth device is disabled");
			System.out.println("Enabling bluetooth device..");
			System.out.println(ba.enable() ? "Success" : "Failed");
		}
		System.out.println("Startig discovery...");
		if (ba.startDiscovery()) {
			System.out.println("Discovering...");
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			activity.registerReceiver(mReciever, filter);
		} else {
			System.out.println("Discovering did not start?");
		}
		System.out.println("Discovery done");
		
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
