package host;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;

public class Configuration {
	private static final int DEFAULT_NUMBER_OF_CLIENTS = 5;
	private int numberOfClients;
	private static final int DEFAULT_NUMBER_OF_BUTTONS = 25;
	private int numberOfButtons;
	private static final int MY_COMPUTER = 182;
	private static final int MY_CALCULATOR = 183;
	private static final int NUM_LOCK = 144;
	private static final int SCROLL_LOCK = 145;
	private File configFile;
	private String configuration;
	private static ArrayList<Integer> keyCodes;
	private static Configuration instance = null;
	
	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	private Configuration() {
		determineConfigFileLocation(); //different path on different operating systems
		if (!configFile.exists()) {
			generateConfiguration();
			writeConfigFile();
		}
		parseConfigFile();
	}
	
	private void addDefaultKeyCodes() {
		keyCodes = new ArrayList<Integer>();
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
				buttonKeyCodes.append("client" + client + ":button" + button + "=" + keyCodes.get(client * DEFAULT_NUMBER_OF_CLIENTS + button) + "\n");
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
	
	private void parseConfigFile() {
		System.out.println("parsing config file...");
		if (configFile.exists()) {
			
		} else {
			System.out.println("config file not found!");
			System.exit(1);
		}
		System.out.println("done");
	}
	
	public int getKeyCode(int clientID, int buttonID) {
		int index = clientID * DEFAULT_NUMBER_OF_CLIENTS + buttonID;
		if (index < keyCodes.size()) {
			return keyCodes.get(clientID * DEFAULT_NUMBER_OF_BUTTONS + buttonID);
		} else {
			throw new IllegalArgumentException("clientID " + clientID + " is too big!");
		}
	}
}
