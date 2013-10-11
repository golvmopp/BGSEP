/*
   Copyright (C) 2013  Isak Eriksson

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

package bgsep.communication;

/**
 * @author Isak Eriksson (isak.eriksson@mail.com)
 * Includes the methods for sending data to the server.
 * This interface can be used for all communication implementations for example Bluetooth or wifi.
 *
 */
public interface Sender {
	
	/**
	 * Sends a message about which button was pressed or released.
	 * @param id a byte from 0 to 19 that represents a button
	 * @param pressed a boolean indicating if the button was pressed, else release
	 * @return a boolean indicating if the parameters were correct
	 */
	public void send(byte id, boolean pressed);
	
	/**
	 * Sends a message about which joystick was moved.
	 * @param id a byte from 0 to 19 that represents a joystick
	 * @param value a value between -1 and 1 describing the joystick's new position
	 * @return a boolean indicating if the parameters were correct
	 */
	public void send(byte id, float value);
	
	/**
	 * Tells the server that the connection will close.
	 * @param message a cause to display in the server
	 * @return
	 */
	public void sendCloseMessage(String message);
	
	/**
	 * Sends the name of the device. This name will represent the device in the server.
	 * @param name the name of the device
	 */
	public void sendNameMessage(String name);
}
