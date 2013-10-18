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

package host;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import bluetooth.BluetoothClient;
import bluetooth.BluetoothServer;

/**
 * 
 * This is a singleton class which will handle the mapping between different
 * {@link BluetoothClient}s buttons and the key codes. A configuration file
 * folder will be created and read from. Configuration files, containing
 * different setups of key code mappings, will be created and parsed.
 * 
 * @author Isak Eriksson (isak.eriksson@mail.com) & Linus Lindren
 *         (linlind@student.chalmers.se)
 * 
 */
public class Configuration {
	private int numberOfClients;
	private int numberOfButtons;
	private File configFile;
	private String configuration;
	private ArrayList<Integer> keyCodes;
	private static Configuration instance = null;


	private Configuration() {
		loadConfig();
	}

	public synchronized static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	/**
	 * @param clientID
	 *            The id of the client
	 * @return A {@link Set} of existing key codes for the given client
	 * @throws IllegalArgumentException
	 */
	public synchronized Set<Integer> getClientKeyCodes(int clientID) throws IllegalArgumentException {
		int offset = clientID * numberOfButtons;
		HashSet<Integer> keycodeSet = new HashSet<>();

		for (int i = offset; i < (offset + numberOfButtons); i++) {
			try {
				keycodeSet.add(this.keyCodes.get(i));
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IllegalArgumentException();
			}
		}

		return keycodeSet;
	}

	/**
	 * Gets the configured key code for a specific client and a button.
	 * 
	 * @param clientID
	 *            the client on which a button was pressed/released
	 * @param buttonID
	 *            the button which was pressed/released
	 * @return the configured key code
	 * @throws IllegalArgumentException
	 *             If the clientID or the buttonID exceeds the allowed values,
	 *             an {@link IllegalArgumentException} is thrown.
	 */

	public synchronized int getKeyCode(int clientID, int buttonID) throws IllegalArgumentException {
		int index = clientID * numberOfButtons + buttonID;
		if (index < keyCodes.size() && buttonID < numberOfButtons) {
			return keyCodes.get(clientID * numberOfButtons + buttonID);
		} else {
			throw new IllegalArgumentException(
					"Too high button id! Ignoring button event.\n(Edit your config file or run the command 'reloadConfiguration' and set a higher number of buttons to prevent this error.)");
		}
	}

	/**
	 * 
	 * Takes input from the user about number of clients and number of buttons.
	 * Then parses the matching configuration file (Creates a new one if
	 * needed). The configuration files is located at
	 * '~/.config/VirtualGamepad'.
	 * 
	 */

	public void loadConfig() {

		numberOfClients = BluetoothServer.getNumberOfClients();
		numberOfButtons = BluetoothServer.getNumberOfButtons();
		keyCodes = new ArrayList<Integer>();

		// Allocating space for array
		for (int i = 0; i < (numberOfClients * numberOfClients) + numberOfButtons; i++) {
			keyCodes.add(0);
		}
		determineConfigFileLocation(); // different path on different operating
										// systems
		if (!configFile.exists()) {
			generateConfiguration();
			writeConfigFile();
		}
		parseConfigFile();

	}

	public int getNumberOfClients() {
		return numberOfClients;
	}

	private void addDefaultKeyCodes() {

		keyCodes = new ArrayList<Integer>();
		for (int i = 65; i <= 249; i++) {
			keyCodes.add(i);
		}

		while (keyCodes.size() <= numberOfClients * numberOfButtons + numberOfButtons) {
			keyCodes.add(0);
		}

	}

	private void determineConfigFileLocation() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Linux")) {
			System.out.println("you are running on a Linux machine, setting config location to ~/.config/VirtualGamepad");
			configFile = new File(System.getProperty("user.home") + File.separatorChar + ".config/VirtualGamepad" + File.separatorChar + "virtual-gamepad-" + numberOfClients + "c"
					+ numberOfButtons + "b.conf");
			System.out.println("config file path \"" + configFile.getAbsolutePath() + "\"");
		} else if (os.startsWith("Windows")) {
			System.out.println("Windoze is not yet supported. Get a Linux based OS now!");
			System.exit(1);
		}
	}

	private void generateConfiguration() {
		addDefaultKeyCodes();
		StringBuilder buttonKeyCodes = new StringBuilder();
		for (int client = 0; client < numberOfClients; client++) {
			for (int button = 0; button < numberOfButtons; button++) {
				int code = client * numberOfButtons + button;
				buttonKeyCodes.append("client" + client + ":button" + button + "=" + keyCodes.get(code) + "\n");
				if (keyCodes.get(code) == 249) {
					System.out.println();
					System.out.println("---------------------------------------------------------");
					System.out.println("NOTIFICATION: THE NUMBER OF DEFAULT KEYCODES HAS EXCEEDED!" + "\nIn \"" + configFile.getAbsolutePath() + "\"\nat [client" + client
							+ ", button" + button + "]" + "\nPLEASE MANUALLY REPLACE ALL ZEROES IN THE CONFIG FILE");
					System.out.println("---------------------------------------------------------");

				}
			}
		}
		this.configuration = "[KeyCodes]\n" + buttonKeyCodes.toString();
	}

	private void writeConfigFile() {
		try {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			FileWriter fw = new FileWriter(configFile);
			fw.write(configuration);
			fw.close();
		} catch (IOException e) {
			System.out.println("error: " + e.getMessage());
			System.exit(1);
		}
	}

	private void parseLine(String line) {
		line = line.split("#")[0];
		System.out.println("Parsed: " + line);
		if (line.contains(":")) {
			int client, button, code;
			client = getClient(line);
			button = getButton(line);
			code = getCode(line);
			/*
			 * System.out.println("client " + client + " : button " + button +
			 * " : code " + code);
			 */
			keyCodes.add(client * numberOfButtons + button, code);
		}
	}

	private void parseConfigFile() {
		String line = "";
		if (configFile.exists()) {
			int lineCounter = 1;
			try {
				FileReader fr = new FileReader(configFile);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					parseLine(line);
					lineCounter++;
				}
				br.close();
			} catch (FileNotFoundException e) {
				parsingError("cannot open config file: " + configFile.getAbsolutePath() + "\n");
			} catch (IOException e) {
				parsingError("ERROR reading config file: " + configFile.getAbsolutePath() + "\n");
			} catch (NumberFormatException e) {
				parsingError("FAILED parsing integer at: '" + line + "' [line "+ lineCounter +"]\nin file: " + configFile.getAbsolutePath() + "\n");
			}
		} else {
			parsingError("config file at path" + configFile.getAbsolutePath() +"\n could not be found!");

		}
	}

	private int getClient(String line) {
		line = line.split(":")[0];
		line = line.split("client")[1];
		int number = Integer.parseInt(line);
		return number;
	}
	
	private void parsingError(String s){
		System.out.println();
		System.out.println(s);
		System.out.println("Please fix the problems in the config file and start the server again!");
		System.exit(1);
	}

	private int getButton(String line) {
		line = line.split(":")[1];
		line = line.split("button")[1];
		line = line.split("=")[0];
		int number = Integer.parseInt(line);
		return number;
	}

	private int getCode(String line) {
		line = line.split("=")[1];
		int number = Integer.parseInt(line);
		return number;
	}

}
