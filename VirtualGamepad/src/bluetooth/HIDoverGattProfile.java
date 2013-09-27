package bluetooth;

import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

@TargetApi(18)
public class HIDoverGattProfile {

	private static final String SIG_HID = "1812";
	private static final String SIG_DEVICE_INFORMATION = "180A";
	private static final String SIG_BATTERY = "180F";
	private static final String SIG_CHARA_BATTERY_LEVEL = "2A19";
	private static final String SIG_CHARA_REPORT_MAP = "2A4B";
	private static final String SIG_CHARA_HID_INFORMATION = "2A4A";
	private static final String SIG_CHARA_HID_CONTROL_POINT = "2A4C";

	private static final String SIG_DESC_PRESENTATION_FORMAT = "2904";
	private static final String SIG_DESC_CLIENT_INFORMATION_CONFIGURATION = "2902";

	private static final String SIG_UNIT_PERCENTAGE = "27AD"; // this is hard coded directly in two bytes
	private static final String SIG_MANUFACTURER_NAME = "2A29";

	public static final String SHORT_UUID_BASE = "00001000800000805F9B34FB"; // used to make 128 bit UUID from 16 bit SIG
	
	private BluetoothGattService HIDService;
	private BluetoothGattService batteryService;
	private BluetoothGattService deviceInfoService;

	
	private static final byte[] GAMEPAD_VALUE = {
				
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
	
	private static final byte[] MOUSE_VALUE = { 0x05, 0x01, // USAGE_PAGE
															// (Generic Desktop)
			0x09, 0x02, // USAGE (Mouse)
			(byte) 0xa1, 0x01, // COLLECTION (Application)
			0x09, 0x01, // USAGE (Pointer)
			(byte) 0xa1, 0x00, // COLLECTION (Physical)
			0x05, 0x09, // USAGE_PAGE (Button)
			0x19, 0x01, // USAGE_MINIMUM (Button 1)
			0x29, 0x03, // USAGE_MAXIMUM (Button 3)
			0x15, 0x00, // LOGICAL_MINIMUM (0)
			0x25, 0x01, // LOGICAL_MAXIMUM (1)
			(byte) 0x95, 0x03, // REPORT_COUNT (3)
			0x75, 0x01, // REPORT_SIZE (1)
			(byte) 0x81, 0x02, // INPUT (Data,Var,Abs)
			(byte) 0x95, 0x01, // REPORT_COUNT (1)
			0x75, 0x05, // REPORT_SIZE (5)
			(byte) 0x81, 0x03, // INPUT (Cnst,Var,Abs)
			0x05, 0x01, // USAGE_PAGE (Generic Desktop)
			0x09, 0x30, // USAGE (X)
			0x09, 0x31, // USAGE (Y)
			0x15, (byte) 0x81, // LOGICAL_MINIMUM (-127)
			0x25, 0x7f, // LOGICAL_MAXIMUM (127)
			0x75, 0x08, // REPORT_SIZE (8)
			(byte) 0x95, 0x02, // REPORT_COUNT (2)
			(byte) 0x81, 0x06, // INPUT (Data,Var,Rel)
			(byte) 0xc0, // END_COLLECTION
			(byte) 0xc0 // END_COLLECTION
	};
	private static final String TAG = "Gamepad";

	public HIDoverGattProfile() {
		createBatteryService();
		createDeviceInfoService();
		createHIDService();
	}

	public boolean addProfileToGATTServer(BluetoothGattServer server) {
		boolean fail = false;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if (!server.addService(batteryService)) { // if HIDService is added before BatteryService something terrible happens...
			fail = true;
			Log.d(TAG, "fail adding BatteryService");
		} else {
			Log.d(TAG, "added BatteryService");
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!server.addService(HIDService)) {
			fail = true;
			Log.d(TAG, "fail adding HIDService");
		} else {
			Log.d(TAG, "added HIDService");
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!server.addService(deviceInfoService)) {
			fail = true;
			Log.d(TAG, "fail adding DeviceInfoService");
		} else {
			Log.d(TAG, "added DeviceInfoService");
		}
		return !fail;
	}

	private BluetoothGattService createHIDService() {
		UUID uuid = createUUID(SIG_HID);
		
		Log.d(TAG, "HID UUID: " + uuid.toString());
		HIDService = new BluetoothGattService(uuid,
				BluetoothGattService.SERVICE_TYPE_PRIMARY);
		BluetoothGattCharacteristic reportMapCharacteristic = new BluetoothGattCharacteristic(
				createUUID(SIG_CHARA_REPORT_MAP),
				BluetoothGattCharacteristic.PROPERTY_READ,
				BluetoothGattCharacteristic.PERMISSION_READ);
		reportMapCharacteristic.setValue(GAMEPAD_VALUE);
		BluetoothGattCharacteristic HIDInformationCharacteristic = new BluetoothGattCharacteristic(
				createUUID(SIG_CHARA_HID_INFORMATION),
				BluetoothGattCharacteristic.PROPERTY_READ,
				BluetoothGattCharacteristic.PERMISSION_READ);
		byte[] HIDInformationValue = { 0x01, 0x11, 26, 0x40 }; // version,
																// version,
																// sweden's
																// country code
																// in decimal,
																// flags 0100
																// 0000
		HIDInformationCharacteristic.setValue(HIDInformationValue);
		BluetoothGattCharacteristic HIDControlPointCharacteristic = new BluetoothGattCharacteristic(
				createUUID(SIG_CHARA_HID_CONTROL_POINT),
				BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
				BluetoothGattCharacteristic.PERMISSION_WRITE);
		HIDService.addCharacteristic(reportMapCharacteristic);
		HIDService.addCharacteristic(HIDInformationCharacteristic);
		HIDService.addCharacteristic(HIDControlPointCharacteristic);
		return HIDService;
	}

	private BluetoothGattService createBatteryService() {
		
		UUID uuid = createUUID(SIG_BATTERY);
		
		Log.d(TAG, "BATTERY UUID: " + uuid.toString());
		
		batteryService = new BluetoothGattService(uuid,
				BluetoothGattService.SERVICE_TYPE_PRIMARY);
		
		BluetoothGattCharacteristic batteryLevel = new BluetoothGattCharacteristic(
				createUUID(SIG_CHARA_BATTERY_LEVEL),
				BluetoothGattCharacteristic.PROPERTY_READ,
				BluetoothGattCharacteristic.PERMISSION_READ);
		
		BluetoothGattDescriptor presentationFormatDescriptor = new BluetoothGattDescriptor(
				createUUID(SIG_DESC_PRESENTATION_FORMAT),
				BluetoothGattDescriptor.PERMISSION_READ);
		
		byte[] presentationFormatValue = { 0x04, 0x00, 0x27, (byte) 0xAD, 0x01,
				0x00, 0x00 };
		presentationFormatDescriptor.setValue(presentationFormatValue); // uint8,
																		// exponent,
																		// unituuid,
																		// unituuid,
																		// namespace,
																		// desiciption,
																		// description
		byte[] characteristicValue = { 0x00, 0x00 };
		BluetoothGattDescriptor clientCharacteristicConfigurationDescriptor = new BluetoothGattDescriptor(
				createUUID(SIG_DESC_CLIENT_INFORMATION_CONFIGURATION),
				BluetoothGattDescriptor.PERMISSION_WRITE);
		clientCharacteristicConfigurationDescriptor
				.setValue(characteristicValue);
		batteryLevel.addDescriptor(presentationFormatDescriptor);
		batteryLevel.addDescriptor(clientCharacteristicConfigurationDescriptor);
		byte[] value = { 100 }; // 100%, our battery is of course completely charged!
		batteryLevel.setValue(value);
		batteryService.addCharacteristic(batteryLevel);
		return batteryService;
	}

	private BluetoothGattService createDeviceInfoService() {
		UUID uuid = createUUID(SIG_DEVICE_INFORMATION);
		
		Log.d(TAG, "DEVICE UUID: " + uuid.toString());
		
		deviceInfoService = new BluetoothGattService(uuid ,
				BluetoothGattService.SERVICE_TYPE_PRIMARY);
		BluetoothGattCharacteristic manufacturerName = new BluetoothGattCharacteristic(
				createUUID(SIG_MANUFACTURER_NAME),
				BluetoothGattCharacteristic.PROPERTY_READ,
				BluetoothGattCharacteristic.PERMISSION_READ);
		manufacturerName.setValue("BGSEP");
		deviceInfoService.addCharacteristic(manufacturerName);
		return deviceInfoService;
	}

	private java.util.UUID createUUID(String SIG) {
		
		String uuid = getUUIDString(SIG, true);
		Log.d(TAG, "CONVERTING " + uuid);
		String formattedUuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20);
		Log.d(TAG, "TO " + formattedUuid);
		return UUID.fromString(formattedUuid); // wingardium leviosa! 10 points to Gryffindor!
	}
	
	private String getUUIDString(String uuidValue, boolean shortUUID) {
        byte[] byteuuidValue;
		
		if (uuidValue == null) {
        	throw new NullPointerException("uuidValue is null");
        }
        int length = uuidValue.length();
        if (shortUUID) {
               if (length < 1 || length > 8) {
            	   		Log.d("gamepad", "LENGTH IS WRONG: " + uuidValue);
                        throw new IllegalArgumentException();
                }
                byteuuidValue = UUIDToByteArray("00000000".substring(length) + uuidValue
                                + SHORT_UUID_BASE);
        } else {
                if (length < 1 || length > 32) {
                        throw new IllegalArgumentException();
                }
                byteuuidValue = UUIDToByteArray("00000000000000000000000000000000".substring(length) + uuidValue);
        }
        return UUIDByteArrayToString(byteuuidValue);
	}
	
	public static byte[] UUIDToByteArray(String uuidStringValue) {
        byte[] uuidValue = new byte[16];
        if (uuidStringValue.indexOf('-') != -1) {
                throw new NumberFormatException("The '-' character is not allowed in UUID: " + uuidStringValue);
        }
        for (int i = 0; i < 16; i++) {
                uuidValue[i] = (byte) Integer.parseInt(uuidStringValue.substring(i * 2, i * 2 + 2), 16);
        }
        return uuidValue;
	}
	
	public static String UUIDByteArrayToString(byte[] uuidValue) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < uuidValue.length; i++) {
                buf.append(Integer.toHexString(uuidValue[i] >> 4 & 0xf));
                buf.append(Integer.toHexString(uuidValue[i] & 0xf));
        }
        return buf.toString();
	}
	
}
