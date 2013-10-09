package bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import lib.Protocol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
	
public class BluetoothHandler extends Thread {
	
	private Activity activity;
	private static final String TAG = "Gamepad";
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private OutputStream outputStream;
	private UUID ExpectedUUID;
	private SenderImpl si;
	private boolean stopped;
	
	public BluetoothHandler(Activity activity) {
		ExpectedUUID = java.util.UUID.fromString(Protocol.SERVER_UUID);
		this.activity = activity;
		stopped = false;
		si = new SenderImpl(this);
		if (initBluetoothAdapter()) {
			connectToServer();
		}
	}
	
	private void connectToServer() {
		boolean serverFound = false;
		if (adapter.getBondedDevices() != null && adapter.getBondedDevices().size() != 0) {
			Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
			for(BluetoothDevice d : adapter.getBondedDevices()){
				Log.d(TAG, "\t" + d.getName());
				serverFound = true; // this should only be done if the server program was found but it does not work yet
				/*for (ParcelUuid uuid : d.getUuids()) {
					Log.d(TAG, "UUID:" + uuid.toString());
					if (uuid.toString().equals(ExpectedUUID.toString())) {
						serverFound = true;
						Log.d(TAG, "Found a gamepad host at device" + d.getName() + " (" + d.getAddress() + ")");
					}
				}*/
				if (serverFound) {
					Log.d(TAG, "Connecting to server..");	
					connect(d.getAddress());
					return;
				}
			}
		}
		Log.d(TAG, "no servers found!");
	}
	
	private boolean initBluetoothAdapter() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			Log.d(TAG,"No bluetooth adapter detected! Stopping thread!");
			stopped = true;
			return false;
		} else {
			Log.d(TAG,"Bluetooth adapter \"" + adapter.getName() + "\" detected");
		}
		if (adapter.isEnabled()) {
			Log.d(TAG,"Bluetooth device is enabled");
		} else {
			Log.d(TAG,"Bluetooth device is disabled");
			Log.d(TAG,"Enabling bluetooth device..");
			Log.d(TAG,adapter.enable() ? "Success" : "Failed");
		}
		return true;
	}
	
	public synchronized void send(byte[] data) {
		try {
			outputStream.write(data);
		} catch (IOException e) {
			Log.d(TAG, "Unable to send data (" + e.getMessage() + "). The server seems to be down, stopping communication..");
			stopped = true;
		} catch (NullPointerException e) {
			Log.d(TAG, "No connection to server, stopping communication..");
		}
	}

	private boolean connect(final String address) {
        if (adapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = adapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        try {
			socket = device.createInsecureRfcommSocketToServiceRecord(ExpectedUUID);
			socket.connect();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return true;
    }
	
	@Override
	public void run() {
		while (!interrupted() && !stopped) {
			si.poll();
			Log.d(TAG, "poll");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showToast(CharSequence text) {
		Context context = activity.getApplicationContext();
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}
}
