package bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import bgsep.virtualgamepad.MainActivity;
import lib.Protocol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
		setName("BluetoothHandler");
		ExpectedUUID = java.util.UUID.fromString(Protocol.SERVER_UUID);
		this.activity = activity;
		stopped = false;
		si = new SenderImpl(this);
		initBluetoothAdapter();
	}
	
	public void disconnect() {
		Log.d(TAG, "disconnecting from server");
		stopped = true;
		si.sendCloseMessage("Disconnected by user");
		notifyDisconnected();
	}
	
	public synchronized void send(byte[] data) {
		if (isConnected()) {
			try {
				outputStream.write(data);
			} catch (IOException e) {
				Log.d(TAG, "Unable to send data (" + e.getMessage() + "). The server seems to be down, stopping communication..");
				disconnect();
			} catch (NullPointerException e) {
				Log.d(TAG, "No connection to server, stopping communication..");
				disconnect();
			}
		}
	}
	
	public boolean isConnected() {
		return (socket != null && socket.isConnected() && !stopped && isAlive());
	}
	
	@Override
	public void run() {
		Log.d(TAG, "running...");
		stopped = false;
		if(!connectToServer()){
			return;
		}
		Log.d(TAG, "server connected, entering poll loop..");
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
	
	/**
	 * Loops through all bounded devices and connects to the device which is running the Virtual Gamepad Host.  
	 * 
	 */
	private boolean connectToServer() {
		Log.d(TAG, "trying to connect to server..");
		boolean serverFound = false;
		if (adapter.getBondedDevices() != null
				&& adapter.getBondedDevices().size() != 0) {
			Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
			for (BluetoothDevice d : adapter.getBondedDevices()) {
				Log.d(TAG, "\t" + d.getName());
				serverFound = checkForServer(d);
				if (serverFound) {
					Log.d(TAG, "Connecting to server..");
					boolean connected = connect(d.getAddress());
					if(connected){
						notifyConnected();
						return true;
					}
				} else {
					System.out.println("start fetching with Sdp on bonded device " + d.getName() + " - " + d.getAddress());
					d.fetchUuidsWithSdp();
				}
			}
			System.out.println("did not find server, trying more..");
			int count = 0;
			while (true) {
				count++;
				for (BluetoothDevice d : adapter.getBondedDevices()) {
					if (checkForServer(d)) {
						Log.d(TAG, "Connecting to server..");
						boolean connected = connect(d.getAddress());
						if(connected){
							notifyConnected();
							return true;
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (count > 10) {
					notifyNoServerFound();
					return false;
				}
			}
		}
		notifyNoServerFound();
		return false;
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
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println("unable to connect to server: " + e.getMessage());
			return false;
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
	
	
	private void notifyConnected() {
		showToast("Connected");
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		    	((MainActivity) activity).serverConnected();
		    }
		});
	}
	
	private void notifyDisconnected() {
		showToast("No connection to server");
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		    	((MainActivity) activity).serverDisconnected();
		    }
		});
	}
	
	private void notifyNoServerFound() {
		Log.d(TAG, "no servers found!");
		showToast("No server found");
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		    	((MainActivity) activity).serverDisconnected();
		    }
		});
	}
}
