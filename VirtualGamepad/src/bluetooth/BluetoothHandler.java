package bluetooth;

import android.bluetooth.BluetoothAdapter;

public class BluetoothHandler {
	public BluetoothHandler() {
		BluetoothAdapter ba = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
		if (ba == null) {
			System.out.println("No bluetooth adapter detected!");
		} else {
			System.out.println("Detected default bluetooth adapter");
		}
			
	}
}
