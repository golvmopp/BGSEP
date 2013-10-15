/*
   Copyright (C) 2013  Linus Lindgren

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 
 * This is a {@link Thread} that, with a given server (
 * {@link StreamConnectionNotifier}), listens to new input streams and tries to
 * create new {@link BluetoothClient}s.
 * 
 * @author Linus Lindgren (linlind@student.chalmers.se)
 * 
 */

public class IncomingClientListener extends Thread {

	private StreamConnectionNotifier socket;

	public IncomingClientListener(StreamConnectionNotifier socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				StreamConnection conn = socket.acceptAndOpen();
				System.out.println("Client connected!");

				DataInputStream dis = new DataInputStream(conn.openInputStream());
				DataOutputStream dos = conn.openDataOutputStream();
				
				BufferedInputStream bis = new BufferedInputStream(dis);
				BufferedOutputStream bos = new BufferedOutputStream(dos);


				BluetoothClient client;
				try {
					client = new BluetoothClient(bis, bos);


					BluetoothServer.getInstance().addClient(client);

					System.out.println("Added client with ID: " + client.getClientId());

					client.start();

				} catch (Exception e) {
					System.out.println("Failed adding client: " + e.getMessage());

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
