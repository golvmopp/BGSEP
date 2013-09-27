package bluetooth;

import java.util.UUID;

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
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;


@TargetApi(18)

	
public class BluetoothHandler {
	
	private Activity activity;
	private static final String TAG = "Gamepad";
	private BluetoothManager manager;
	private BluetoothAdapter adapter;

	
	public BluetoothHandler(Activity activity) {
		
		this.activity = activity;

		manager = (BluetoothManager)activity.getSystemService(Context.BLUETOOTH_SERVICE);

		adapter = manager.getAdapter();

		if(!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			Log.d(TAG, "FAIL");
		}else{
			Log.d(TAG, "WIN");
		}
		
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
		

		
		BluetoothGattServerCallback bgsc = new BluetoothGattServerCallback() {
		};
		
		BluetoothGattServer gattServer = manager.openGattServer(activity, bgsc);
		gattServer.clearServices();
		
		HIDoverGattProfile hogp = new HIDoverGattProfile();
		Log.d(TAG, "Creating hogp: " + (hogp.addProfileToGATTServer(gattServer) ? "succeded" : "failed"));
		

		for(BluetoothGattService s : gattServer.getServices()){
			Log.d(TAG, "SERVICE: " + s.getUuid());
			Log.d(TAG, "HAS INCLUDED SERVICES: " + s.getIncludedServices().size());

			for(BluetoothGattCharacteristic c : s.getCharacteristics()){
				Log.d(TAG, "CHARACTERISTIC " + c.getInstanceId() + "\n\tWITH PERMISION " + c.getPermissions() + "\n\tWITH PROPERTIES " + c.getProperties());
				
				for(BluetoothGattDescriptor d : c.getDescriptors()){
					Log.d(TAG, "DECRIPTOR " + d.getUuid() + " WITH VALUES " + d.getValue().toString());
				}
			}
			
		}
		BluetoothDevice device = null;
		
		BluetoothGattCallback callback = new BluetoothGattCallback() {
			@Override
			public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
				Log.d(TAG, "status: " + status + ", newState: " + newState);
			}
		};

		if(adapter.getBondedDevices() != null && adapter.getBondedDevices().size() != 0){
			Log.d(TAG, adapter.getBondedDevices().size() + " bounded devices");
			for(BluetoothDevice d : adapter.getBondedDevices()){
				Log.d(TAG, "\t" + d.getName());
				gattServer.connect(d, true);
				Log.d(TAG, "CONNECT: ");
				Log.d(TAG, "CONNECTION STATE FOR DEVICE " + manager.getConnectionState(d, BluetoothProfile.GATT));

				device = d;
			}
		}

		
	/*	while(true){
			for(BluetoothDevice bd : manager.getConnectedDevices(BluetoothProfile.GATT)){
				Log.d(TAG,"SENDING RESPONSE");
				gattServer.sendResponse(bd, 0, 0, 0, this.gamePadDescriptor);
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		LeDeviceScanActivity scanActivity = new LeDeviceScanActivity();
		Log.d(TAG, "STARTING LE SCAN: " + scanActivity.scan(adapter));
        
		
	}
	
    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
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
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
       // mBluetoothGatt = gattS
     //   Log.d(TAG, "Trying to create a new connection.");
//        mBluetoothDeviceAddress = address;
        //mConnectionState = STATE_CONNECTING;
        return true;
    }
    
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //intentAction = ACTION_GATT_CONNECTED;
                //mConnectionState = STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //intentAction = ACTION_GATT_DISCONNECTED;
                //mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");

            }
        }

    };


}
