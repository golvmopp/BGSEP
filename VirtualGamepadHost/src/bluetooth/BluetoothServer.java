/*
Copyright (C) 2013 Linus Lindgren

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;

import util.IdHandler;

/**
 * 
 * The BluetoothServer's main tasks is to start up the
 * {@link IncomingClientListener} and to keep track of the active clients (
 * {@link BluetoothClient}) connected to the server.
 * 
 * @author Linus Lindgren(linlind@student.chalmers.se)
 * 
 */
public class BluetoothServer {
	private static BluetoothServer instance;
	private static HashMap<Integer, BluetoothClient> clients;

	private final static int MAX_NUMBER_CLIENTS = 10;
	private final static int MAX_NUMBER_BUTTONS = 25;
	private final static UUID VIRTUALGAMEPAD_UUID = new UUID("27012f0c68af4fbf8dbe6bbaf7aa432a", false);
	private final static String NAME = "Virtual Gamepad Host";
	private final static String URL = "btspp://localhost:" + VIRTUALGAMEPAD_UUID + ";name=" + NAME + ";authenticate=false;encrypt=false;";

	private static boolean allowClientInput;
	
	private LocalDevice device;
	private StreamConnectionNotifier server;

	private BluetoothServer() {
		clients = new HashMap<Integer, BluetoothClient>();
		try {
			device = LocalDevice.getLocalDevice();
			if (device != null) {
				if (!LocalDevice.isPowerOn()) {
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

	/**
	 * The constructor of BluetoothServer checks if the computer has a
	 * {@link LocalDevice} and if it work's properly, otherwise the application
	 * will exit.
	 * 
	 */
	public static BluetoothServer getInstance() {
		if (instance == null) {
			instance = new BluetoothServer();
		}
		return instance;
	}

	/**
	 * Opens a server connection ({@link StreamConnectionNotifier}) and starts
	 * up {@link IncomingClientListener} if succeeded. If opening of the
	 * connection failed, the application will be exited.
	 */
	public void startServer() {
		System.out.println();
		System.out.println();
		System.out.println("Device:");
		System.out.println("\t" + device.getFriendlyName());
		System.out.println("\t" + device.getBluetoothAddress());
		System.out.println();
		System.out.println();
		try {
			System.out.println("Opening up server connection...");
			server = (StreamConnectionNotifier) Connector.open(URL);
		} catch (IOException e) {
			System.out.println("Failed open server connection: " + e.getMessage());
			System.exit(1);
		}
		System.out.println("Server up and running!");
		IncomingClientListener listener = new IncomingClientListener(server);
		listener.start();
		setAllowClientInput(true);

	}

	public void addClient(BluetoothClient client) {
		clients.put(client.getClientId(), client);
	}

	public void removeClient(BluetoothClient client) {
		clients.remove(client.getClientId());
		IdHandler.getInstance(Configuration.getInstance().getNumberOfClients()).setIdUnoccupied(client.getClientId());
	}

	public BluetoothClient getClient(int id) {
		return clients.get(id);
	}

	public HashMap<Integer, BluetoothClient> getClients() {
		return clients;
	}

	/**
	 * A static method that ask for a user input in the form of an integer with
	 * values between 1 and 25 to set the number of buttons per client.
	 * 
	 * @return Returns the number of buttons per client.
	 */
	public static int getNumberOfButtons() {

		try {
			System.out.println();
			System.out.println("Please enter the number of buttons [1-" + MAX_NUMBER_BUTTONS + "] (default=" + lib.Constants.DEFAULT_NUMBER_OF_BUTTONS + "):");
			int i = getInputInt(1, MAX_NUMBER_BUTTONS);
			System.out.println("Number of buttons is set to " + i);
			System.out.println();
			return i;
		} catch (IOException e) {
		} catch (NumberFormatException e) {
		} catch (WrongNumberArgsException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Number of buttons is set to default: " + lib.Constants.DEFAULT_NUMBER_OF_BUTTONS);
		System.out.println();
		return lib.Constants.DEFAULT_NUMBER_OF_BUTTONS;

	}

	/**
	 * A static method that ask for a user input in the form of an integer with
	 * values between 1 and 10 to set the number of clients.
	 * 
	 * @return Returns the number of clients.
	 */
	public static int getNumberOfClients() {
		try {
			System.out.println();
			System.out.println("Please enter the maximal number of clients [1-" + MAX_NUMBER_CLIENTS + "] (default=" + lib.Constants.DEFAULT_MAX_CLIENTS + "):");
			int i = getInputInt(1, MAX_NUMBER_CLIENTS);
			System.out.println("Maximal number of clients is set to " + i);
			System.out.println();
			return i;

		} catch (IOException e) {
		} catch (NumberFormatException e) {
		} catch (WrongNumberArgsException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Maximal number of clients is set to default: " + lib.Constants.DEFAULT_MAX_CLIENTS);
		System.out.println();
		return lib.Constants.DEFAULT_MAX_CLIENTS;

	}
	
	public static boolean isAllowClientInput() {
		return allowClientInput;
	}

	public static void setAllowClientInput(boolean allowClientInput) {
		BluetoothServer.allowClientInput = allowClientInput;
	}

	private static int getInputInt(int lowest, int highest) throws IOException, NumberFormatException, WrongNumberArgsException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		int i = Integer.parseInt(br.readLine());
		if (i >= lowest && i <= highest) {
			return i;
		} else {
			throw new WrongNumberArgsException("Number not between " + lowest + " and " + highest);
		}
	}

}