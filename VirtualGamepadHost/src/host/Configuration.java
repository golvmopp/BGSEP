package host;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import bluetooth.BluetoothServer;

public class Configuration {
	private int numberOfClients;
	private int numberOfButtons;

	private File configFile;
	private String configuration;
	private static ArrayList<Integer> keyCodes;
	private static Configuration instance = null;

	public synchronized static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	/**
	 * Gets the configured key code for a specific client and a button.
	 * 
	 * @param clientID
	 *            the client on which a button was pressed/released
	 * @param buttonID
	 *            the button which was pressed/released
	 * @return the configured key code
	 */
	public synchronized int getKeyCode(int clientID, int buttonID) {
		int index = clientID * numberOfClients + buttonID;
		if (index < keyCodes.size()) {
			return keyCodes.get(clientID * numberOfButtons + buttonID);
		} else {
			throw new IllegalArgumentException("clientID " + clientID + " is too big!");
		}
	}
	/**
	 * 
	 * Takes input from the user about number of clients and number of buttons. Then parses the matching
	 * configuration file (Creates a new one if needed).
	 * The configuration files is located at '~/.config/VirtualGamepad'.
	 * 
	 */
	
	public void loadConfig(){

		numberOfClients = BluetoothServer.getNumberOfClients();
		numberOfButtons = BluetoothServer.getNumberOfButtons();
		keyCodes = new ArrayList<Integer>();
		determineConfigFileLocation(); // different path on different operating
										// systems
		if (!configFile.exists()) {
			generateConfiguration();
			writeConfigFile();
		}
		parseConfigFile();
	
	}


	private Configuration() {
		loadConfig();
	}
	
	private void addDefaultKeyCodes() {
		/*
		 * for (int i = 1; i <= 7; i++) { keyCodes.add(i); } for (int i = 21; i
		 * <= 26; i++) { keyCodes.add(i); } for (int i = 124; i <= 249; i++) {
		 * if (i != MY_COMPUTER && i != MY_CALCULATOR && i != NUM_LOCK && i !=
		 * SCROLL_LOCK) { keyCodes.add(i); } }
		 */
		for (int i = 65; i <= 249; i++) {
			keyCodes.add(i);
		}

		while (keyCodes.size() < 240) {
			keyCodes.add(0);
		}

	}

	private void determineConfigFileLocation() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Linux")) {
			System.out.println("you are running on a Linux machine, setting config location to ~/.config/VirtualGamepad");
			configFile = new File(System.getProperty("user.home") + File.separatorChar + ".config/VirtualGamepad" + File.separatorChar + "virtual-gamepad-"+ numberOfClients +"c" + numberOfButtons +"b.conf");
			System.out.println("config file path \"" + configFile.getAbsolutePath()+ "\"");
		} else if (os.startsWith("Windows")) {
			System.out.println("Windoze is not yet supported. Get a Linux based OS now!");
			System.exit(1);
		}
	}

	private void generateConfiguration() {
		addDefaultKeyCodes();
		System.out.println("generating config file...");
		StringBuilder buttonKeyCodes = new StringBuilder();
		for (int client = 0; client < numberOfClients; client++) {
			for (int button = 0; button < numberOfButtons; button++) {
				int code = client * numberOfButtons + button;
				buttonKeyCodes.append("client" + client + ":button" + button + "=" + keyCodes.get(code) + "\n");
				if (keyCodes.get(code) == 249) {
					System.out.println();
					System.out.println("---------------------------------------------------------");
					System.out.println("NOTIFICATION: THE NUMBER OF DEFAULT KEYCODES HAS EXCEEDED!" + "\nIn \"" + configFile.getAbsolutePath() + "\"\nat [client" + client + ", button" + button + "]"
							+ "\nPLEASE MANUALLY REPLACE ALL ZEROES IN THE CONFIG FILE");
					System.out.println("---------------------------------------------------------");

				}
			}
		}
		this.configuration = "[KeyCodes]\n" + buttonKeyCodes.toString();
		System.out.println("done");
	}

	private void writeConfigFile() {
		System.out.println("writing new configuration...");
		try {
			configFile.getParentFile().mkdirs();
			configFile.createNewFile();
			FileWriter fw = new FileWriter(configFile);
			fw.write(configuration);
			fw.close();
		} catch (IOException e) {
			System.out.println("error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("done");
	}

	private void parseNumberOfClients(String line) {
		int number = Integer.parseInt(line.split("=")[1]);
		System.out.println("setting number of clients to " + number);
		numberOfClients = number;
	}

	private void parseLine(String line) {
		if (line.contains("NumberOfClients")) {
			parseNumberOfClients(line);
		} else {
			if (line.contains(":")) {
				int client, button, code;
				client = getClient(line);
				button = getButton(line);
				code = getCode(line);
				// System.out.println("client " + client + " : button " + button
				// + " : code " + code);
				keyCodes.add(client * numberOfClients + button, code);
			}
		}
	}

	private void parseConfigFile() {
		System.out.println("parsing config file...");
		String line = "";
		if (configFile.exists()) {
			try {
				FileReader fr = new FileReader(configFile);
				BufferedReader br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					parseLine(line);
				}
				br.close();
			} catch (FileNotFoundException e) {
				System.out.println("cannot open config file");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("error reading config file");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.println("failed parsing integer at line: " + line);
				e.printStackTrace();
			}
		} else {
			System.out.println("config file not found!");
			System.exit(1);
		}
		System.out.println("done");
	}

	private int getClient(String line) {
		line = line.split(":")[0];
		line = line.split("client")[1];
		int number = Integer.parseInt(line);
		return number;
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
