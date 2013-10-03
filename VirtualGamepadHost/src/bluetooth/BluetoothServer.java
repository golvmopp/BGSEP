package bluetooth;

import java.io.IOException;
import java.util.HashSet;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer {

	private final static UUID uuid = new UUID("27012f0c68af4fbf8dbe6bbaf7aa432a", false);
	private final static String name = "Virtual Gamepad Host";
	private static final String url = "btspp://localhost:" + uuid + ";name=" + name + ";authenticate=false;encrypt=false;";;

	private static HashSet<BluetoothClient> clients;
	private LocalDevice device;
	private StreamConnectionNotifier server;

	public BluetoothServer() {
		clients = new HashSet<BluetoothClient>();
		try {
			device = LocalDevice.getLocalDevice();

			if (device != null) {

				if (LocalDevice.isPowerOn()) {
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
					IncommingClientListener listener = new IncommingClientListener(server);
					listener.start();

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

	public static void addClient(BluetoothClient client) {
		clients.add(client);
	}

	public static HashSet<BluetoothClient> getClients() {
		return clients;
	}
}
