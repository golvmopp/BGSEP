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
	byte[] gamePadDescriptor = {
			
			0x05, 0x01, //; USAGE_PAGE (Generic Desktop)
			 0x09, 0x05, //; USAGE (Gamepad)
			 (byte) 0xa1, 0x01, //; COLLECTION (Application)
			 0x05, 0x09,// ; USAGE_PAGE (Button)
			 0x19, 0x01, //; USAGE_MINIMUM (Button 1)
			 0x29, 0x09, //; USAGE_MAXIMUM (Button 9)
			 0x15, 0x00, //; LOGICAL_MINIMUM (0)
			 0x25, 0x01, //; LOGICAL_MAXIMUM (1)
			 0x75, 0x01, //; REPORT_SIZE (1)
			 (byte) 0x95, 0x09, //; REPORT_COUNT (9)
			 (byte) 0x81, 0x02, //; INPUT (Data,Var,Abs)
			 (byte) 0x95, 0x07, // REPORT_COUNT (7)
			 (byte) 0x81, 0x03, // INPUT (Cnst,Var,Abs)
			 (byte) 0xC0// ; END_COLLECTION
			
	};
	
	private Activity activity;
	private static final String TAG = "Gamepad";
	private BluetoothManager manager;
	private BluetoothAdapter adapter;

	
	public static String HID_UUID = "00001124-0000-1000-8000-00805f9b34fb";
	
	public BluetoothHandler(Activity activity) {
		
		this.activity = activity;

		manager = (BluetoothManager)activity.getSystemService(Context.BLUETOOTH_SERVICE);

		adapter = manager.getAdapter();

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
		UUID hid = UUID.fromString(HID_UUID);

		BluetoothGattService hidService = new BluetoothGattService(hid, BluetoothGattService.SERVICE_TYPE_PRIMARY);
		
		BluetoothGattCharacteristic charact = new BluetoothGattCharacteristic(UUID.randomUUID(), BluetoothGattCharacteristic.FORMAT_UINT16, BluetoothGattCharacteristic.PERMISSION_READ);
		
		BluetoothGattDescriptor desc = new BluetoothGattDescriptor(UUID.randomUUID(), BluetoothGattDescriptor.PERMISSION_READ);
		desc.setValue(gamePadDescriptor);
		
		charact.addDescriptor(desc);
		
		hidService.addCharacteristic(charact);
		
		if(gattServer.addService(hidService)){
			Log.d(TAG, "SUCCEEDED ADDING HID SERVICE");
		}else{
			Log.d(TAG, "FAILED ADDING HID SERVICE");
		}
		
		if(!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			Log.d(TAG, "FAIL");
		}else{
			Log.d(TAG, "WIN");
		}

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
				BluetoothGatt bg = d.connectGatt(activity, true, callback);
				Log.d(TAG, "CONNECT: ");
				Log.d(TAG, "CONNECTION STATE FOR DEVICE " + manager.getConnectionState(d, BluetoothProfile.GATT));

				device = d;
			}
		}


		while(true){
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
		}
       // LeDeviceScanActivity scanActivity = new LeDeviceScanActivity();
		//Log.d(TAG, "STARTING LE SCAN: " + scanActivity.scan(adapter));
        
		
	}
	
}
