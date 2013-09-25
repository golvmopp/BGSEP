package Bluetooth;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;



public class BluetoothHandler {

	public static void main(String[] args) {
		try {
			LocalDevice ld = LocalDevice.getLocalDevice();
			System.out.println("Your bluetooth device name: " + ld.getFriendlyName());
			System.out.println("Your bluetooth device address: " + ld.getBluetoothAddress());
			
			if(LocalDevice.isPowerOn()){
				System.out.println("Power of device is ON");
				System.out.println("DISCOVERABLE: " + ld.getDiscoverable());
				
			}else{
				System.out.println("Power is OFF, closing analysis");
				System.exit(1);
			}
			
		} catch (BluetoothStateException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		System.exit(0);
	}

}
