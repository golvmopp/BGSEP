package host;

import java.io.*;
import java.util.ArrayList;

public class Configuration {
	private static final int DEFAULT_NUMBER_OF_CLIENTS = 2;
	private int numberOfClients;
	private static final int DEFAULT_NUMBER_OF_BUTTONS = 15;
	private int numberOfButtons;
	private static final int MY_COMPUTER = 182;
	private static final int MY_CALCULATOR = 183;
	private static final int NUM_LOCK = 144;
	private static final int SCROLL_LOCK = 145;
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
	
	private Configuration() {
		keyCodes = new ArrayList<Integer>();
		determineConfigFileLocation(); //different path on different operating systems
		if (!configFile.exists()) {
			generateConfiguration();
			writeConfigFile();
		}
		parseConfigFile();
	}
	
	private void addDefaultKeyCodes() {
		/*
		for (int i = 1; i <= 7; i++) {
			keyCodes.add(i);
		}
		for (int i = 21; i <= 26; i++) {
			keyCodes.add(i);
		}
		for (int i = 124; i <= 249; i++) {
			if (i != MY_COMPUTER && i != MY_CALCULATOR && i != NUM_LOCK && i != SCROLL_LOCK) { 
				keyCodes.add(i);
			}
		}*/
		for (int i = 65; i <= 111; i++) {
			keyCodes.add(i);
		}
		
	}
	
	private void determineConfigFileLocation() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Linux")) {
			System.out.println("you are running on a Linux machine, setting config location to ~/.config/");
			configFile = new File(System.getProperty("user.home") + File.separatorChar + ".config" + File.separatorChar + "virtual-gamepad.conf");
		} else if (os.startsWith("Windows")) {
			System.out.println("Windoze is not yet supported. Get a Linux based OS now!");
			System.exit(1);
		}
	}
	
	private void generateConfiguration() {
		addDefaultKeyCodes();
		System.out.println("generating config file...");
		String general = "NumberOfClients=" + DEFAULT_NUMBER_OF_CLIENTS;
		StringBuilder buttonKeyCodes = new StringBuilder();
		for (int client = 0; client < DEFAULT_NUMBER_OF_CLIENTS; client++) {
			for (int button = 0; button < DEFAULT_NUMBER_OF_BUTTONS; button++) {
				buttonKeyCodes.append("client" + client + ":button" + button + "=" + keyCodes.get(client * DEFAULT_NUMBER_OF_BUTTONS + button) + "\n");
			}
		}
		this.configuration = "[General]\n" + general + "\n\n[KeyCodes]\n" + buttonKeyCodes.toString();
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
				System.out.println("client " + client + " : button " + button + " : code " + code);
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
	
	/**
	 * Gets the configured key code for a specific client and a button. 
	 * @param clientID the client on which a button was pressed/released
	 * @param buttonID the button which was pressed/released
	 * @return the configured key code
	 */
	public synchronized int getKeyCode(int clientID, int buttonID) {
		int index = clientID * DEFAULT_NUMBER_OF_CLIENTS + buttonID;
		if (index < keyCodes.size()) {
			return keyCodes.get(clientID * DEFAULT_NUMBER_OF_BUTTONS + buttonID);
		} else {
			throw new IllegalArgumentException("clientID " + clientID + " is too big!");
		}
	}
}
