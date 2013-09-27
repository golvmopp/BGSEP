package bluetooth;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Handler;
import android.util.Log;

@TargetApi(18)
public class LeDeviceScanActivity extends ListActivity {
	private Handler mHandler;
	private LeScanCallback callback;
	private static final long SCAN_PERIOD = 10000;
	private static final String TAG = "gamepad";
	
	public LeDeviceScanActivity(){
		mHandler = new Handler();
		
		callback = new LeScanCallback() {
			
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Log.d(TAG, "FOUND A DEVICE: " + device.getName() + " ["+ device.getAddress() + "]");
					}
				});
			}
		};
		
	}
	
	public boolean scan(final BluetoothAdapter adapter){

		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				adapter.stopLeScan(callback);
				Log.d(TAG,"STOP LESCAN");
			}
		}, SCAN_PERIOD);
		
		return adapter.startLeScan(callback);
	}
}
