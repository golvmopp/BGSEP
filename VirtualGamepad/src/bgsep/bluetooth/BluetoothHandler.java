/* Copyright (C) 2013  Isak Eriksson

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

package bgsep.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import bgsep.virtualgamepad.MainActivity;
import bgsep.bluetooth.SenderImpl;
import lib.Constants;
import lib.Protocol;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * This is the main bluetooth handler. It handles discovery of and connection to
 * the server. It also notifies the GUI about connection state.
 * 
 * @author Isak Eriksson (isak.eriksson@mail.com) & Linus Lindgren
 *         (linlind@student.chalmers.se)
 * 
 */
public class BluetoothHandler extends Thread {

	private Activity activity;
	private static final String TAG = "Gamepad";
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private UUID ExpectedUUID;
	private SenderImpl si;
	private boolean allowAutoConnect;
	private Toast mainToast;
	private String serverName;
	private static final int SLEEP_BETWEEN_CONNECTION_ATTEMPTS = 3000;
	private static final int SLEEP_BETWEEN_POLL = 2000;
	private static final int SLEEP_BEFORE_STARTING_POLL = 100;

	private enum State {
		IDLE, CONNECTING, WAITING_FOR_ACCEPT, CONNECTED, START_CONNECTING
	}

	private State state;
	public static final int BLUETOOTH_REQUEST_CODE = 1;

	public BluetoothHandler(Activity activity) {
		setName("BluetoothHandler");
		ExpectedUUID = java.util.UUID.fromString(Protocol.SERVER_UUID);
		state = State.IDLE;
		allowAutoConnect = true;
		this.activity = activity;
		mainToast = new Toast((MainActivity) activity);
		si = new SenderImpl(this);
		start();
	}

	public void disconnect(boolean expected, String message) {
		state = State.IDLE;
		if (expected) {
			Log.d(TAG, "disconnecting from server");
			si.sendCloseMessage("Disconnected by user");
			notifyDisconnected(message);
		} else {
			notifyDisconnected(message);
		}
		try {
			Thread.sleep(Constants.SLEEP_BETWEEN_NOTIFY_AND_CLOSE);
			outputStream.close();
			inputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			Log.d(TAG, "no connection to server");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends an array of bytes to the server.
	 * 
	 * @param data
	 *            the data that will be sent to the server
	 */
	public synchronized void send(byte[] data) {
		try {
			outputStream.write(data);
			return;
		} catch (IOException e) {
			Log.d(TAG, "Unable to send data (" + e.getMessage() + "). The server seems to be down, stopping communication..");
			disconnect(false, "Connection interrupted");
		} catch (NullPointerException e) {
			Log.d(TAG, "No connection to server, stopping communication..");
			disconnect(false, "Disconnected");
		}
		state = State.IDLE;
	}

	/**
	 * Stops the attempt to find and connect to a server.
	 */
	public void cancelConnectionAttempt() {
		Log.d(TAG, "cancelling connection attempt");
		((MainActivity) activity).serverDisconnected();
		state = State.IDLE;
	}

	public void startThread() {
		if (!isConnected()) {
			state = State.START_CONNECTING;
		}
	}

	public void autoConnect() {
		if (allowAutoConnect) {
			allowAutoConnect = false;
			startThread();
		}
	}

	// public boolean isStarted() {
	// return false;
	// }

	public boolean isConnected() {
		return (socket != null && state == State.CONNECTED);
	}

	private void notifyConnecting() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				((MainActivity) activity).indicateConnecting();
			}
		});
	}

	@Override
	public void run() {
		while (!interrupted()) {
			switch (state) {
			case START_CONNECTING:
				state = State.CONNECTING;
				notifyConnecting();
				if (initBluetoothAdapter()) {
					startConnectionAttempt();
				} else {
					notifyDisconnected("Bluetooth not available");
				}
				break;
			case WAITING_FOR_ACCEPT:
				readFromServer();
				break;
			default:
				// do nothing...
				break;
			}
			try {
				Thread.sleep(SLEEP_BEFORE_STARTING_POLL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (!interrupted() && state == State.CONNECTED) {
				readFromServer();
				if (state == State.CONNECTED)
					si.poll();

				Log.d(TAG, "poll");
				try {
					Thread.sleep(SLEEP_BETWEEN_POLL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void readFromServer() {
		try {
			if ((state == State.CONNECTED || state == State.WAITING_FOR_ACCEPT) && inputStream.available() > 0) {
				int data = inputStream.read();
				if (data == Protocol.MESSAGE_TYPE_CLOSE) {
					disconnect(false, "Disconnected from server");
				} else if (data == Protocol.MESSAGE_TYPE_SERVER_FULL) {
					disconnect(false, "Server is full");
				} else if (data == Protocol.MESSAGE_TYPE_CONNECTION_ACCEPTED) {
					notifyConnected(serverName);
					state = State.CONNECTED;
				}
			}
		} catch (IOException e) {
			Log.d(TAG, "unable to read incomming data");
		}
	}

	private void startConnectionAttempt() {
		Log.d(TAG, "connecting...");
		if (!connectToServer()) {
			state = State.IDLE;
		} else {
			state = State.WAITING_FOR_ACCEPT;
			si.sendNameMessage(adapter.getName());
			Log.d(TAG, "connection established, waiting for accept..");
		}
	}

	private boolean checkForServer(BluetoothDevice d) {
		ParcelUuid[] uuids = d.getUuids();
		if (uuids == null) {
			return false;
		}
		for (ParcelUuid uuid : uuids) {
			if (uuid.toString().equals(ExpectedUUID.toString())) {
				Log.d(TAG, "Found a gamepad host at device" + d.getName() + " (" + d.getAddress() + ")");
				return true;
			}
		}
		return false;
	}

	private boolean connectToBoundedDevices() {
		Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
		for (BluetoothDevice d : adapter.getBondedDevices()) {

			if (d != null) {
				Log.d(TAG, "\t" + d.getName());
				if (checkForServer(d)) {
					Log.d(TAG, "Connecting to server..");
					boolean connected = connect(d.getAddress());
					if (connected) {
						serverName = d.getName();
						return true;
					}
				} else {
					Log.d(TAG, "start fetching with Sdp on bonded device " + d.getName() + " - " + d.getAddress());
					d.fetchUuidsWithSdp();
				}
			}
		}
		return false;
	}

	/**
	 * Loops through all bounded devices and connects to the device which is
	 * running the Virtual Gamepad Host. If a server is not found on a bounded
	 * device, an SDP discovery is started.
	 * 
	 */
	private boolean connectToServer() {
		Log.d(TAG, "searching for servers..");
		if (adapter.getBondedDevices() == null) {
			notifyDisconnected("Bluetooth not available");
			return false;
		}
		if (adapter.getBondedDevices().size() == 0) {
			notifyNoServerFound();
		}
		while (true) { // start connecting
			if (connectToBoundedDevices()) {
				return true;
			}
			try {
				Thread.sleep(SLEEP_BETWEEN_CONNECTION_ATTEMPTS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (state != State.CONNECTING) {
				return false;
			}
		}
	}

	private boolean initBluetoothAdapter() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			Log.d(TAG, "No bluetooth adapter detected! Stopping thread!");
			state = State.IDLE;
			return false;
		} else {
			Log.d(TAG, "Bluetooth adapter \"" + adapter.getName() + "\" detected");
		}
		if (adapter.isEnabled()) {
			Log.d(TAG, "Bluetooth device is enabled");
		} else {
			Log.d(TAG, "Bluetooth device is disabled");
			Log.d(TAG, "Enabling bluetooth device..");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE);
		}
		return adapter.isEnabled();
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
			inputStream = socket.getInputStream();
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
	 * This is used to display a message to the user about changed connection
	 * state.
	 * 
	 * @param text
	 */
	private void showToast(final CharSequence text) {
		System.out.println("toastar!");
		activity.runOnUiThread(new Runnable() {
			public void run() {
				mainToast.cancel();
				mainToast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
				mainToast.show();
			}
		});
	}

	private void notifyConnected(String name) {
		showToast("Connected to " + name);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				((MainActivity) activity).serverConnected();
			}
		});
	}

	private void notifyDisconnected(String message) {
		showToast(message);
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
