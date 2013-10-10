package host;

import util.Terminal;
import bluetooth.BluetoothServer;

/**
 * 
 * The main class that starts up the {@link BluetoothServer} and the {@link Terminal}
 * 
 * @author Linus Lindgren (linlind@student.chalmers.se) & Isak Eriksson (isak.eriksson@mail.com)
 *
 */

public class Main {
	public static void main(String[] args){
		System.out.println("VIRTUAL GAMEPAD HOST");
		Configuration.getInstance();
		try {
			BluetoothServer server = BluetoothServer.getInstance();
			server.startServer();
			
			Terminal terminal = new Terminal();
			terminal.start();
			
		} catch (Exception e) {
			System.exit(1);
		}

	} 
}