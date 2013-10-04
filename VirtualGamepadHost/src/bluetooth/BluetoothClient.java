package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;
import lib.Protocol;
import util.ClientIdGenerator;

public class BluetoothClient extends Thread {
	private DataInputStream dis;
	private int clientId;
	private String name = "";

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
		String recieved = "";
		for (int i = 0; i < length; i++) {
			byte b = byteArray[i];
			recieved += byteArray[i];
		}
		System.out.println("recieved " + recieved);
		
		switch (id) {
		case Protocol.MESSAGE_TYPE_BUTTON:
			System.out.println("button" + byteArray[1] + (byteArray[1] == 0x01 ? " pressed " : " released"));
			break;
		case Protocol.MESSAGE_TYPE_JOYSTICK:
			float position;
			int bits = 0;
			bits += (int) (byteArray[2] << 24);
			bits += (int) (byteArray[3] << 16);
			bits += (int) (byteArray[4] << 8);
			bits += (int) (byteArray[5]);
			position = Float.intBitsToFloat(bits);
			System.out.println("axis " + byteArray[1] + " moved to position " + position);
			break;
		case Protocol.MESSAGE_TYPE_CLOSE:
			byte[] causeBytes = new byte[length - 1];
			System.arraycopy(byteArray, 1, causeBytes, 0, length - 1);
			String cause = new String(causeBytes);
			System.out.println("client closes connection: " + cause);
			break;
		case Protocol.MESSAGE_TYPE_NAME:
			byte[] nameBytes = new byte[length - 1];
			System.arraycopy(byteArray, 1, nameBytes, 0, length - 1);
			String name = new String (nameBytes);
			System.out.println("new name: " + name);
			this.name = name;
			break;
		case Protocol.MESSAGE_TYPE_POLL:
			System.out.println("poll");
			break;
		}
		
		
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int id) {
		this.clientId = id;
	}
}
