/*
   Copyright (C) 2013  Isak Eriksson, Linus Lindgren

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

package bluetooth;

import host.Configuration;
import host.Joystick;

import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import lib.Constants;
import lib.Protocol;
import util.IdHandler;

/**
 * 
 * The BluetoothClient is a {@link Thread} that represents a client to the
 * {@link BluetoothServer} and contains a {@link DataInputStream} from the
 * client's bluetooth device. The thread interprets the incoming data from the
 * client and uses a {@link Robot} to give key presses on the keyboard.
 * 
 * @author Linus Lindgren(linlind@student.chalmers.com) & Isak
 *         Eriksson(isak.eriksson@mail.com)
 * 
 */
public class BluetoothClient extends Thread {
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private int clientId;
	private String clientName;
	private Robot robot;
	private boolean running;
	private long lastPoll;
	private HashMap<Integer, Joystick> joyStick;
	private static final int SLEEP_BETWEEN_READINGS = 10;

	/**
	 * Constructor that tries to get a client ID. If the server is full the
	 * constructor will throw an exception and close the {@link DataInputStream}
	 * .
	 * 
	 * @param dis
	 *            The data input stream from the client.
	 * @throws Exception
	 *             Is thrown when the server is full.
	 */

	public BluetoothClient(BufferedInputStream bis, BufferedOutputStream bos) throws Exception {

		this.bis = bis;
		this.bos = bos;

		setClientId(IdHandler.getInstance(Configuration.getInstance().getNumberOfClients()).getUnoccupiedId());
		if (getClientId() == -1) {
			serverFull();
			throw new Exception("Server is full!");
		}
		lastPoll = System.currentTimeMillis();
		robot = new Robot();
		joyStick = new HashMap<Integer, Joystick>();
		clientName = "";
	}

	/**
	 * While the thread is running, it will constantly collect data from the
	 * input stream and interpret the data by the standard described in
	 * {@link Protocol}.
	 */
	@Override
	public void run() {
		running = true;
		ArrayList<Byte> data = new ArrayList<Byte>();
		boolean escape = false;
		boolean arrayStopped = true;
	
		
		while (!interrupted() && running) {
			
			try {
				byte[] byteArray = new byte[Constants.MESSAGE_MAX_SIZE];
				int len = 0;
				if(bis.available() >0)
					len = bis.read(byteArray);
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
				System.out.println("Unable to read data from client");
				disconnect();
			}
			if (System.currentTimeMillis() - lastPoll >= lib.Constants.CLIENT_TIMEOUT) {
				System.out.println("Client with ID " + getClientId() + " timed out!");
				disconnect();
			}
			try {
				Thread.sleep(SLEEP_BETWEEN_READINGS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disconnects the client by releasing all pressed keys, stopping the
	 * {@link Thread}, stopping the {@link Joystick}, closing the
	 * {@link DataInputStream} and removing the client from the
	 * {@link BluetoothServer}.
	 */
	public void disconnect() {
		releaseKeys();
		BluetoothServer.getInstance().removeClient(this);
		try {
			this.bis.close();
			this.bos.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		running = false;
		System.out.println((this.clientName.isEmpty() ? "Client" + this.clientId : this.clientName) + " was disconnected");
	}
	
	/**
	 * Sends an indication to the client that the connection was accepted 
	 * 
	 */
	public void connectionAccepted() {
		try {
			bos.write(Protocol.MESSAGE_TYPE_CONNECTION_ACCEPTED);
			bos.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private void serverFull() {
		try {
			bos.write(Protocol.MESSAGE_TYPE_SERVER_FULL);
			Thread.sleep(Constants.SLEEP_BETWEEN_NOTIFY_AND_CLOSE);
			bos.close();
			bis.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void kick() {
		try {
			bos.write(Protocol.MESSAGE_TYPE_CLOSE);
			Thread.sleep(Constants.SLEEP_BETWEEN_NOTIFY_AND_CLOSE);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		disconnect();
	}
	
	private void releaseKeys() {
		for (Integer keyCode : Configuration.getInstance().getClientKeyCodes(clientId)) {
			this.robot.keyRelease(keyCode);
		}
		for (Joystick j : joyStick.values()) {
			j.setStopped();
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

	private void handleButtonEvent(ArrayList<Byte> data) {
		if (BluetoothServer.isAllowClientInput()) {
			try {
				int i = Configuration.getInstance().getKeyCode(clientId, data.get(1));

				if (data.get(2) == 0x01) {
					if (i != 0)
						robot.keyPress(i);
				} else {
					if (i != 0)
						robot.keyRelease(i);
				}
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void handleJoystickEvent(ArrayList<Byte> data) {
		if (BluetoothServer.isAllowClientInput()) {
			byte[] floatByte = { data.get(2), data.get(3), data.get(4), data.get(5) };
			float position = java.nio.ByteBuffer.wrap(floatByte).asFloatBuffer().get();
			int joyStickId = data.get(1);
			if (!joyStick.containsKey(joyStickId)) {
				this.joyStick.put(joyStickId, new Joystick(joyStickId, clientId));
			}
			joyStick.get(joyStickId).setNewValue(position);
		}
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

}
