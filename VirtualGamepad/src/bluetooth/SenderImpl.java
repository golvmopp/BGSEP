package bluetooth;

import java.lang.reflect.Array;
import java.util.Arrays;

import lib.Protocol;

public class SenderImpl implements Sender {

	BluetoothHandler bh;
	
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
		byte[] data = new byte [5];
		data[0] = id;
		data[1] = (byte) ((floatbits >> 24) & 0xFF);
		data[2] = (byte) ((floatbits >> 16) & 0xFF);
		data[3] = (byte) ((floatbits >> 8) & 0xFF);
		data[4] = (byte) (floatbits & 0xFF);
		send(data, Protocol.MESSAGE_TYPE_JOYSTICK);
		return false;
	}

	@Override
	public boolean send(String message) {
		byte[] data = message.getBytes();
		send(data, Protocol.MESSAGE_TYPE_CLOSE);
		return false;
	}
	
	private byte[] insertEscapeBytes(byte[] data) {
		
		for (int i = 0; i < data.length; i++) {
			
		}
		return 
	}
	
	private void send(byte[] data, byte type) {
		byte[] allData = new byte[data.length + 3]; 
		allData[0] = Protocol.START;
		allData[1] = type;
		System.arraycopy(data, 0, allData, 1, data.length);
		allData[length + 2] = Protocol.STOP;
		bh.send(data);
	}

}
