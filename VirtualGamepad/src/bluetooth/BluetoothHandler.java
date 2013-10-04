package bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import lib.Protocol;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
	
public class BluetoothHandler {
	
	private Activity activity;
	private static final String TAG = "Gamepad";
	private BluetoothManager manager;
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private OutputStream outputStream;
	
	public BluetoothHandler(Activity activity) {
		this.activity = activity;
		initBluetoothAdapter();
		connectToBondedDevice();
		startSendingTestData();
	}
	
	private void startSendingTestData() {
		SenderImpl si = new SenderImpl(this);
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "Sending data..");
			si.send((byte) 0x03, true);	
			si.send((byte) 0x04, 0.4f);
			si.send((byte) 0x04, -0.6f);
			si.send((byte) 0x03, false);
			si.send("test");
		}
	}
	
	private void connectToBondedDevice() {
		if (adapter.getBondedDevices() != null && adapter.getBondedDevices().size() != 0){
			Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
			for(BluetoothDevice d : adapter.getBondedDevices()){
				Log.d(TAG, "\t" + d.getName());
				Log.d(TAG, "Connecting to " + d.getName());
				connect(d.getAddress());
			}
		}
	}
	
	private void initBluetoothAdapter() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
			Log.d(TAG,"No bluetooth adapter detected!");
			return;
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
	}
	
	public void send(byte [] data) {
		try {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean connect(final String address) {
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
			socket = device.createInsecureRfcommSocketToServiceRecord(java.util.UUID.fromString(Protocol.SERVER_UUID));
			socket.connect();
			outputStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return true;
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
       // mBluetoothGatt = gattS
     //   Log.d(TAG, "Trying to create a new connection.");
//        mBluetoothDeviceAddress = address;
        //mConnectionState = STATE_CONNECTING;
    }
}