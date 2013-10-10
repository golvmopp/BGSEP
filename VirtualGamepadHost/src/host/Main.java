package host;

import util.Terminal;
import bluetooth.BluetoothServer;

public class Main {
	public static void main(String[] args){
		System.out.println("VIRTUAL GAMEPAD HOST");
		Configuration.getInstance();
		new BluetoothServer();
		Terminal terminal = new Terminal();
		terminal.start();
	} 
}