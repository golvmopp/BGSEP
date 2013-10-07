package bluetooth;

import java.nio.ByteBuffer;
import bgsep.communication.Sender;
import android.util.Log;
import lib.Protocol;

/**
 * 
 * This class implements Sender using bluetooth.
 *
 */
public class SenderImpl implements Sender {

	private BluetoothHandler bh;
	private static final String TAG = "Gamepad";
	
	public SenderImpl(BluetoothHandler bh) {
		this.bh = bh;
	}
	
	@Override
	public boolean send(byte id, boolean pressed) {
		byte[] data = new byte[2];
		data[0] = id;
		data[1] = (byte) (pressed ? 0x01 : 0x00);
		send(data, Protocol.MESSAGE_TYPE_BUTTON);
		return false;
	}

	@Override
	public boolean send(byte id, float value) {
		int floatbits = Float.floatToIntBits(value);
		Log.d(TAG, "floatbits == " + Integer.toBinaryString(floatbits));
		byte[] data = new byte[5];
		data[0] = id;
		//data[1] = (byte) ((floatbits >> 24) & 0xFF);
		//data[2] = (byte) ((floatbits >> 16) & 0xFF);
		//data[3] = (byte) ((floatbits >> 8) & 0xFF);
		//data[4] = (byte) (floatbits & 0xFF);
		byte[] floatArray = ByteBuffer.allocate(4).putFloat(value).array();
		System.arraycopy(floatArray, 0, data, 1, 4);
		send(data, Protocol.MESSAGE_TYPE_JOYSTICK);
		return false;
	}

	@Override
	public boolean send(String message) {
		byte[] data = message.getBytes();
		send(data, Protocol.MESSAGE_TYPE_CLOSE);
		return false;
	}
	
	private boolean shouldBeEscaped(byte b) {
		return b == Protocol.ESCAPE || b == Protocol.START || b == Protocol.STOP;
	}
	
	/**
	 * This method takes a byte array and inserts the ESCAPE byte
	 * before all occurrences of START, STOP and ESCAPE except from
	 * the first START and the last STOP.
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
		logData(allData);
		bh.send(insertEscapeBytes(allData));
	}
	
	private void logData(byte[] data) {
		String dataString = "";
		for (int i = 0; i < data.length; i++) {
			dataString += ((int) data[i]) + " "; 
		}
		Log.d(TAG, dataString);
	}
	
	public void poll() {
		send(new byte[0], Protocol.MESSAGE_TYPE_POLL);
	}

}
