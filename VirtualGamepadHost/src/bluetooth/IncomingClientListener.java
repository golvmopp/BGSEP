package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class IncomingClientListener extends Thread {

	private StreamConnectionNotifier server;

	public IncomingClientListener(StreamConnectionNotifier server) {
		this.server = server;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				StreamConnection conn = server.acceptAndOpen();
				System.out.println("Client connected!");

				DataInputStream dis = new DataInputStream(conn.openInputStream());

				BluetoothClient client;
				try {
					client = new BluetoothClient(dis);

					BluetoothServer.addClient(client);
					
					System.out.println("Added client with ID: " + client.getClientId());
					
					client.start();

				} catch (Exception e) {
					System.out.println("Failed adding client: " + e.getMessage());

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
