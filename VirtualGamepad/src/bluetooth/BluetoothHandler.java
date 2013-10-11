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

/**
 * @author Isak Eriksson (isak.eriksson@mail.com) & Linus Lindgren (linlind@student.chalmers.se)
 * This is the main bluetooth handler. It handles discovery of and connection to the server.
 * It also notifies the GUI about connection state.
 *
 */
public class BluetoothHandler extends Thread {
	
	private Activity activity;
	private static final String TAG = "Gamepad";
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private OutputStream outputStream;
	private UUID ExpectedUUID;
	private SenderImpl si;
	private boolean stopped;
	private boolean connect;
	
	public BluetoothHandler(Activity activity) {
		setName("BluetoothHandler");
		ExpectedUUID = java.util.UUID.fromString(Protocol.SERVER_UUID);
		this.activity = activity;
		stopped = true;
		connect = false;
		si = new SenderImpl(this);
		initBluetoothAdapter();
		start();
	}
	
	public void disconnect(boolean expected) {
		if (expected) {
			si.sendCloseMessage("Disconnected by user");
		}
		try {
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "disconnecting from server");
		stopped = true;
		notifyDisconnected(expected);
	}
	
	/**
	 * Sends an array of bytes to the server.
	 * @param data the data that will be sent to the server
	 */
	public synchronized void send(byte[] data) {
		try {
			outputStream.write(data);
		} catch (IOException e) {
			Log.d(TAG, "Unable to send data (" + e.getMessage() + "). The server seems to be down, stopping communication..");
			disconnect(false);
		} catch (NullPointerException e) {
			Log.d(TAG, "No connection to server, stopping communication..");
			disconnect(false);
		}
	}

	public void startThread() {
		connect = true;
	}
	
	public boolean isStarted() {
		return !stopped;
	}
	
	public boolean isConnected() {
		return (socket != null && socket.isConnected() && !stopped);
	}
	
	@Override
	public void run() {
		while (!interrupted()) {
			if (connect) {
				Log.d(TAG, "connecting...");
				stopped = false;
				if(!connectToServer()){
					stopped = true;
				} else {
					Log.d(TAG, "server connected, entering poll loop..");
					si.sendNameMessage(adapter.getName()); // send the device name to server
				}
				connect = false;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
	
	private boolean checkBoundedDevices() {
		Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
		for (BluetoothDevice d : adapter.getBondedDevices()) {
			Log.d(TAG, "\t" + d.getName());
			if (checkForServer(d)) {
				Log.d(TAG, "Connecting to server..");
				boolean connected = connect(d.getAddress());
				if(connected){
					notifyConnected();
					return true;
				}
			} else {
				Log.d(TAG, "start fetching with Sdp on bonded device " + d.getName() + " - " + d.getAddress());
				d.fetchUuidsWithSdp();
			}
		}
		return false;
	}
	
	/**
	 * Loops through the bounded devices to check if expected UUID was fetched.
	 * If so, tries to connect to it.
	 * @return true if a server was found and successfully connected to
	 */
	private boolean checkIfUUIDFetchedWithSDP() {
		int count = 0;
		while (true) {
			count++;
			for (BluetoothDevice d : adapter.getBondedDevices()) {
				if (checkForServer(d)) {
					Log.d(TAG, "Connecting to server..");
					if (connect(d.getAddress())) {
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
	
	/**
	 * Loops through all bounded devices and connects to the device which is running the Virtual Gamepad Host.
	 * If a server is not found on a bounded device, an SDP discovery is started.  
	 * 
	 */
	private boolean connectToServer() {
		Log.d(TAG, "searching for servers..");
		if (adapter.getBondedDevices() == null || adapter.getBondedDevices().size() == 0) {
			notifyNoServerFound();
			return false;
		}
		if (checkBoundedDevices()) {
			Log.d(TAG, "server found in bounded devices");
			return true;
		}
		Log.d(TAG, "did not find server, starting to search for fetched UUIDs");
		checkIfUUIDFetchedWithSDP();
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
	
	/**
	 * This is used to display a message to the user about changed connection state.
	 * @param text
	 */
	private void showToast(final CharSequence text) {
		System.out.println("toastar!");
		activity.runOnUiThread(new Runnable() {
		    public void run() {
		        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
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
	
	private void notifyDisconnected(boolean expected) {
		if (expected) {
			showToast("Disconnected");
		} else {
			showToast("No connection to server");
		}
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
