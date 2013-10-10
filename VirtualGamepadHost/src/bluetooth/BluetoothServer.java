package bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

import util.ClientIdGenerator;

public class BluetoothServer {

	private final static UUID uuid = new UUID("27012f0c68af4fbf8dbe6bbaf7aa432a", false);
	private final static String name = "Virtual Gamepad Host";
	private static final String url = "btspp://localhost:" + uuid + ";name=" + name + ";authenticate=false;encrypt=false;";;
	private static HashMap<Integer, BluetoothClient> clients;
	private static int numberOfPlayers; 

	private LocalDevice device;
	private StreamConnectionNotifier server;

	public BluetoothServer() {
		clients = new HashMap<Integer, BluetoothClient>();
		try {
			device = LocalDevice.getLocalDevice();
			if (device != null) {
				if (LocalDevice.isPowerOn()) {
					startServer();
				} else {
					System.out.println("Device power is off. Turn it on!");
					System.exit(1);
				}
			} else {
				System.out.println("No device");
				System.exit(1);
			}
		} catch (BluetoothStateException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	private void startServer() {
		System.out.println();
		System.out.println("------------------------------------");
		System.out.println("Device:");
		System.out.println("\t" + device.getFriendlyName());
		System.out.println("\t" + device.getBluetoothAddress());
		System.out.println("------------------------------------");
		System.out.println();
		try {
			System.out.println("Opening up server connection...");
			server = (StreamConnectionNotifier) Connector.open(url);
		} catch (IOException e) {
			System.out.println("Failed open server connection: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("Server up and running!");
		IncomingClientListener listener = new IncomingClientListener(server);
		listener.start();
	}
	
	public static void addClient(BluetoothClient client) {
		clients.put(client.getClientId(), client);
	}
	
	public static void removeClient(BluetoothClient client){
		clients.remove(client.getClientId());
		ClientIdGenerator.getInstance().removeClient(client.getClientId());
	}

	public static BluetoothClient getClient(int id) {
		return clients.get(id);
	}
	
	public static HashMap<Integer, BluetoothClient> getClients() {
		return clients;
	}
	
	private static void getNumberOfClients(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Please enter the maximal number of clients [1-10] (default=5):");
			int i = br.read();
			if(i>0 && i<= 10){
				System.out.println("Maximal number of clients is set to " + i);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}