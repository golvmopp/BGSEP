package bluetooth;

import host.Configuration;
import host.Joystick;

import java.awt.Robot;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lib.Protocol;
import util.ClientIdGenerator;

public class BluetoothClient extends Thread {
	private int clientId;
	private String clientName = "";
	private Robot robot;
	private DataInputStream dis;
	private boolean running;
	private long lastPoll;
	private HashMap<Integer, Joystick> joyStick;

	public BluetoothClient(DataInputStream dis) throws Exception {
		this.dis = dis;
		setClientId(ClientIdGenerator.getInstance().getGeneratedId());
		if (getClientId() == -1) {
			throw new Exception("Server is full!");
		}
		lastPoll = System.currentTimeMillis();
		robot = new Robot();
		joyStick = new HashMap<Integer, Joystick>();
	}

	@Override
	public void run() {
		running = true;
		ArrayList<Byte> data = new ArrayList<Byte>();
		boolean escape = false;
		boolean arrayStopped = true;
		while (!interrupted() && running) {
			try {
				byte[] byteArray = new byte[1000];
				int len = dis.read(byteArray);
				for (int i = 0; i < len; i++) {
					byte b = byteArray[i];
					if (arrayStopped) {
						if (b == Protocol.START) {
							if (escape) {
								escape = false;
								data.add(b);
							} else {
								arrayStopped = false;
							}
						}
					} else {
						if (!(b == Protocol.STOP || b == Protocol.ESCAPE)) {
							data.add(b);
							if (escape) {
								escape = false;
							}
						} else if (b == Protocol.ESCAPE) {
							if (escape) {
								data.add(b);
								escape = false;
							} else {
								escape = true;
							}
						} else if (b == Protocol.STOP) {
							if (escape) {
								escape = false;
								data.add(b);
							} else {
								try {
									interpretByteArray(data);
								} catch (IndexOutOfBoundsException e) {
									System.out.println("WRONG BYTE ARRAY: " + e.getMessage());
								}
								data = new ArrayList<Byte>();
								arrayStopped = true;
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (System.currentTimeMillis() - lastPoll >= lib.Constants.CLIENT_TIMEOUT) {
				System.out.println("Client with ID " + getClientId() + " timed out!");
				disconnect();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect() {
		for (Joystick j : joyStick.values()) {
			j.setStopped();
		}
		BluetoothServer.removeClient(this);
		running = false;
	}

	private void handleButtonEvent(ArrayList<Byte> data) {
		try {
			if (data.get(2) == 0x01) {
				//System.out.println("Pressing key " +
				//Configuration.getInstance().getKeyCode(clientId, data.get(1)));
				robot.keyPress(Configuration.getInstance().getKeyCode(clientId, data.get(1)));
			} else {
				//System.out.println("Releasing key " +
				//		Configuration.getInstance().getKeyCode(clientId, data.get(1)));
				robot.keyRelease(Configuration.getInstance().getKeyCode(clientId, data.get(1)));
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Failed getting key code: " + e.getMessage());
		}
	}
	
	private void handleJoystickEvent(ArrayList<Byte> data) {
		byte[] floatByte = { data.get(2), data.get(3), data.get(4), data.get(5) };
		float position = java.nio.ByteBuffer.wrap(floatByte).asFloatBuffer().get();
		int joyStickId = data.get(1);		
		if (!joyStick.containsKey(joyStickId)) {
			System.out.println("button " + joyStickId + " on client " + clientId + " is a joystick, creating a Joystick for it..");
			this.joyStick.put(joyStickId, new Joystick(joyStickId, clientId));
		}
		//System.out.println("received position " + position + " on ID " + joyStickId);
		joyStick.get(joyStickId).setNewValue(position);
	}
	
	private void handleCloseEvent(ArrayList<Byte> data) {
		byte[] stringBytes = new byte[data.size() - 1];
		for (int i = 1; i < data.size(); i++) {
			stringBytes[i - 1] = data.get(i);
		}
		String message = new String(stringBytes);
		System.out.println("Client closes connection: " + message);
		disconnect();
	}
	
	private void handleNameEvent(ArrayList<Byte> data) {
		byte[] stringBytes2 = new byte[data.size() - 1];
		for (int i = 1; i < data.size(); i++) {
			stringBytes2[i - 1] = data.get(i);
		}
		String name = new String(stringBytes2);
		System.out.println("Client name: " + name);
		setClientName(name);
	}
	
	private void interpretByteArray(ArrayList<Byte> data) throws IndexOutOfBoundsException {
		int id = data.get(0);
		switch (id) {
		case Protocol.MESSAGE_TYPE_BUTTON:
			handleButtonEvent(data);
			break;
		case Protocol.MESSAGE_TYPE_JOYSTICK:
			handleJoystickEvent(data);
			break;
		case Protocol.MESSAGE_TYPE_CLOSE:
			handleCloseEvent(data);
			break;
		case Protocol.MESSAGE_TYPE_NAME:
			handleNameEvent(data);
			break;
		case Protocol.MESSAGE_TYPE_POLL:
			lastPoll = System.currentTimeMillis();
			break;
		}
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int id) {
		this.clientId = id;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
