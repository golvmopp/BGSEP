package bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import bgsep.virtualgamepad.MainActivity;
import lib.Protocol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Looper;
import android.os.ParcelUuid;
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

	private boolean checkForServer(BluetoothDevice d) {
		for (ParcelUuid uuid : d.getUuids()) {
			Log.d(TAG, "UUID:" + uuid.toString());
			if (uuid.toString().equals(ExpectedUUID.toString())) {
				Log.d(TAG, "Found a gamepad host at device" + d.getName() + " (" + d.getAddress() + ")");
				return true;
			}
		}
		return false;
	}
	
	private void connectToServer() {
		boolean serverFound = false;
		if (adapter.getBondedDevices() != null
				&& adapter.getBondedDevices().size() != 0) {
			Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
			for (BluetoothDevice d : adapter.getBondedDevices()) {
				Log.d(TAG, "\t" + d.getName());
				serverFound = checkForServer(d);
				if (serverFound) {
					Log.d(TAG, "Connecting to server..");
					connect(d.getAddress());
					return;
				} else {
					System.out.println("start fetching with Sdp on bonded device " + d.getName() + " - " + d.getAddress());
					d.fetchUuidsWithSdp();
				}
			}
			System.out.println("Did not found server, trying more!");
			int count = 0;
			while (true) {
				count++;
				for (BluetoothDevice d : adapter.getBondedDevices()) {
					if (checkForServer(d)) {
						Log.d(TAG, "Connecting to server..");
						connect(d.getAddress());
						return;
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (count > 10) {
					Log.d(TAG, "no servers found!");
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
			notifyDisconnected();
		} catch (NullPointerException e) {
			Log.d(TAG, "No connection to server, stopping communication..");
			notifyDisconnected();
		}
	}
	
	private void notifyDisconnected() {
		stopped = true;
		showToast("No connection to server");
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
			if (socket.isConnected()) {
				showToast("Connected");
			}
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
	
	private void showToast(final CharSequence text) {
		System.out.println("toastar!");
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
		    }
		});
	}
}
