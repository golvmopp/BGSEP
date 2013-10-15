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

package util;

import host.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import bluetooth.BluetoothClient;
import bluetooth.BluetoothServer;

/**
 * 
 * This is a terminal designed for the {@link BluetoothServer}. It's a
 * {@link Thread} that will listen to inputs from the user and will interpret
 * some specified commands: <i>halt</i> <i>kick</i> <i>list</i> <i>reloadConfig
 * </i> <i>logProtocol</i> <i>help</i><i>f (toggle freeze)</i>
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
		halt, kick, list, reloadConfig, show, help, f
	}

	public void run() {
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
						+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(lib.Constants.SHORT_LICENSE);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
				+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
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
			if (line != null) {
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
				case reloadConfig:
					reloadConfiguration(arguments);
					break;
				case show:
					show(arguments);
					break;
				case f:
					toggleFreeze();
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

	private void show(String[] arguments) {
		if (arguments.length > 1) {
			if (arguments[1].equals("w")) {
				System.out.println(lib.Constants.WARRANTY);
			} else if (arguments[1].equals("c")) {
				System.out.println(lib.Constants.CONDITIONS);
			}
		} else {
			System.out.println("Illegal argument: use w or c");
		}
	}

	private void halt() {
		System.out.println("Server will now halt");
		System.exit(0);
	}
	
	private void toggleFreeze() {
		if(BluetoothServer.isAllowClientInput()){
			BluetoothServer.setAllowClientInput(false);
			System.out.println("Input freezed");
		}else{
			BluetoothServer.setAllowClientInput(true);
			System.out.println("Input unfreezed");
		}
	}

	private void kick(String[] arguments) throws IOException {
		if (arguments.length > 1) {
			BluetoothServer.getInstance().getClient(Integer.parseInt(arguments[1])).disconnect();
			;
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
			if(c.toString().equals("f")){
				System.out.print(c.toString() + " (");
				System.out.println("Quickly freeze/unfreeze all clients - useful when you don't want the clients to mess with the terminal input)");
			}else{
				System.out.println(c.toString());
			}
		}
	}

	private void reloadConfiguration(String[] arguments) {
		Configuration.getInstance().loadConfig();
	}
}