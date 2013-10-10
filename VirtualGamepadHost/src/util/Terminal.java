package util;

import host.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import bluetooth.BluetoothClient;
import bluetooth.BluetoothServer;

/**
 * 
 * This is a terminal designed for the {@link BluetoothServer}. It's a {@link Thread} that will
 * listen to inputs from the user and will interpret some specified commands:
 * <i>halt</i> <i>kick</i> <i>list</i> <i>reloadConfiguration </i>
 * <i>logProtocol</i> <i>help</i>
 * 
 * 
 * @author Linus Lindgren (linlind@student.chalmers.se) & Isak Eriksson
 *         (isak.eriksson@mail.com)
 * 
 */

public class Terminal extends Thread {
	private BufferedReader br;
	private static final String name = "virtual-gamepad$ ";

	public Terminal() {
		setName("Terminal");
	}

	private enum Command {
		halt, kick, list, reloadConfiguration, logProtocol, help
	}

	public void run() {
		br = new BufferedReader(new InputStreamReader(System.in));
		while (!interrupted()) {
			nextCommand();
		}
	}

	private void nextCommand() {
		Command command;
		String[] arguments;
		System.out.println("");
		System.out.print(name);
		try {
			String line = br.readLine();
			if (!(line == null)) {
				arguments = line.split("\\s+");
				try {
					command = Command.valueOf(arguments[0]);
				} catch (IllegalArgumentException e) {
					System.out.println(arguments[0] + ": Command not found");
					System.out.println("");
					command = Command.help;
				}
				switch (command) {
				case halt:
					halt();
					break;
				case kick:
					kick(arguments);
					break;
				case list:
					list();
					break;
				case help:
					help();
					break;
				case reloadConfiguration:
					reloadConfiguration(arguments);
					break;
				case logProtocol:
					logProtocol(arguments);
					break;
				default:
					System.out.println(arguments[0] + ": Command not found");
					break;
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void logProtocol(String[] arguments) {
		if (arguments.length > 1) {
			if (arguments[1].equals("1")) {
				System.out.println("Protocol logging enabled");
			} else if (arguments[1].equals("0")) {
				System.out.println("Protocol logging disabled");
			} else {
				System.out.println("Illegal argument: use 1 or 0");
			}
		} else {
			System.out.println("Illegal argument: use 1 or 0");
		}
	}

	private void halt() {
		System.out.println("Server will now halt");
		System.exit(0);
	}

	private void kick(String[] arguments) throws IOException {
		if (arguments.length > 1) {
			BluetoothServer.getInstance().getClient(Integer.parseInt(arguments[1])).disconnect();;
		} else {
			System.out.println("clients online:");
			for (BluetoothClient client : BluetoothServer.getInstance().getClients().values()) {
				String name = client.getClientName();
				int id = client.getClientId();
				if (client.getName().isEmpty()) {
					name = "[noname]";
				}
				System.out.println("\t" + name + ", id:" + id);
			}
		}
	}

	private void list() {
		System.out.println("clients online:");
		for (BluetoothClient client : BluetoothServer.getInstance().getClients().values()) {
			String name = client.getClientName();
			int id = client.getClientId();
			if (client.getName().isEmpty()) {
				name = "[noname]";
			}
			System.out.println("\t" + name + ", id:" + id);
		}

	}

	private void help() {
		System.out.println("Available commands:");
		System.out.println("");
		for (Command c : Command.values()) {
			System.out.println(c.toString());
		}
	}

	private void reloadConfiguration(String[] arguments) {
		Configuration.getInstance().loadConfig();
	}
}