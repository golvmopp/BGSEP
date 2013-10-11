/* Copyright (C) 2013  Isak Eriksson

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/  */

package bgsep.bluetooth;

import java.nio.ByteBuffer;

import bgsep.bluetooth.BluetoothHandler;
import bgsep.communication.Sender;
import android.util.Log;
import lib.Protocol;

/**
 * @author Isak Eriksson (isak.eriksson@mail.com)
 * This class implements Sender using bluetooth.
 *
 */
public class SenderImpl implements Sender {

	private BluetoothHandler bh;
	private static final String TAG = "Gamepad";
	
	@Override
	public void send(byte id, boolean pressed) {
		byte[] data = new byte[2];
		data[0] = id;
		data[1] = (byte) (pressed ? 0x01 : 0x00);
		send(data, Protocol.MESSAGE_TYPE_BUTTON);
	}
	
	public SenderImpl(BluetoothHandler bh) {
		this.bh = bh;
	}
	
	@Override
	public void send(byte id, float value) {
		int floatbits = Float.floatToIntBits(value);
		Log.d(TAG, "floatbits == " + Integer.toBinaryString(floatbits));
		byte[] data = new byte[5];
		data[0] = id;
		byte[] floatArray = ByteBuffer.allocate(4).putFloat(value).array();
		System.arraycopy(floatArray, 0, data, 1, 4);
		send(data, Protocol.MESSAGE_TYPE_JOYSTICK);
	}

	@Override
	public void sendCloseMessage(String message) {
		byte[] data = message.getBytes();
		send(data, Protocol.MESSAGE_TYPE_CLOSE);
	}
	
	@Override
	public void sendNameMessage(String name) {
		byte[] data = name.getBytes();
		send(data, Protocol.MESSAGE_TYPE_NAME);
	}
	
	public void poll() {
		send(new byte[0], Protocol.MESSAGE_TYPE_POLL);
	}
	
	private boolean shouldBeEscaped(byte b) {
		return b == Protocol.ESCAPE || b == Protocol.START || b == Protocol.STOP;
	}
	
	/**
	 * This method takes a byte array and inserts the ESCAPE byte
	 * before all occurrences of START, STOP and ESCAPE except from
	 * the first and last byte.
	 * @param data the unescaped array
	 * @return the escaped aray
	 */
	private byte[] insertEscapeBytes(byte[] data) {
		byte[] escapingBytes = new byte[1000];
		int offset = 0;
		for (int i = 0; i < data.length; i++) {
			if (shouldBeEscaped(data[i]) && i > 0 && i < data.length - 1) { //the first and last byte should not be escaped
				escapingBytes[i + offset] = Protocol.ESCAPE; //prefix the byte with the ESCAPE byte
				offset++;
			}
			escapingBytes[i + offset] = data[i];
		}
		byte[] escapedBytes = new byte[data.length + offset];
		System.arraycopy(escapingBytes, 0, escapedBytes, 0, escapedBytes.length);
		return escapedBytes;
	}
	
	private synchronized void send(byte[] data, byte type) {
		byte[] allData = new byte[data.length + 3];
		allData[0] = Protocol.START;
		allData[1] = type;
		System.arraycopy(data, 0, allData, 2, data.length);
		allData[data.length + 2] = Protocol.STOP;
		bh.send(insertEscapeBytes(allData));
	}
}