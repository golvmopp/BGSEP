package host;

import bluetooth.BluetoothServer;

public class Main {
	public static void main(String[] args){
		System.out.println("VIRTUAL GAMEPAD HOST");
		Configuration configuration = Configuration.getInstance();
		BluetoothServer bs = new BluetoothServer();
	} 
}