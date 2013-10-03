package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

import util.ClientIdGenerator;

public class BluetoothClient extends Thread {
	private DataInputStream dis;
	private int clientId;

	public BluetoothClient(DataInputStream dis) {
		this.dis = dis;
		setClientId(ClientIdGenerator.getGeneratedId());

	}

	@Override
	public void run() {
		super.run();

		while (!interrupted()) {

			try {
				byte[] byteArray = new byte[1000];

				int len = dis.read(byteArray);
				interpretByteArray(byteArray, len);

			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void interpretByteArray(byte[] byteArray, int length) {
		int id = byteArray[0];
		
		
		for (int i = 0; i < length; i++) {
			byte b = byteArray[i];
			System.out.println("Byte collected: " + (int)b);
		}
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int id) {
		this.clientId = id;
	}
}
