package bluetooth;

import java.util.Arrays;

public class SenderImpl implements Sender {

	BluetoothHandler bh;
	
	/*
	 * messages:
	 * 		0 		first byte specifies message type
	 * 		1... 	the following bytes are message type specific
	 * 
	 * message types:
	 *  	Button		0
	 *  	Joystick	1
	 *  	Close		2
	 *  	Name		3
	 *  	Poll		4
	 *  
	 * specifics:		byte	1		2		3		4		5		6
	 *		Button:				id		bool
	 *		Joystick:			id		float.....................      (32 bit float == 4 bytes)
	 *  	Close				String.................................				
	 * 		Name				String.................................
	 *  	Poll
	 */
	private static final byte MESSAGE_TYPE_BUTTON = 0X00;
	private static final byte MESSAGE_TYPE_JOYSTICK = 0X01;
	private static final byte MESSAGE_TYPE_CLOSE = 0X02;
	private static final byte MESSAGE_TYPE_NAME = 0X03;
	private static final byte MESSAGE_TYPE_POLL = 0X04;
	
	
	public SenderImpl(BluetoothHandler bh) {
		this.bh = bh;
	}
	
	@Override
	public boolean send(byte id, boolean pressed) {
		byte[] data = new byte[2];
		data[0] = id;
		data[1] = (byte) (pressed ? 0x01 : 0x00);
		send(data, MESSAGE_TYPE_BUTTON);
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
		send(data, MESSAGE_TYPE_JOYSTICK);
		return false;
	}

	@Override
	public boolean send(String message) {
		byte[] data = message.getBytes();
		send(data, MESSAGE_TYPE_CLOSE);
		return false;
	}
	
	private void send(byte[] data, byte type) {
		byte[] allData = new byte[data.length +1]; 
		allData[0] = type;
		System.arraycopy(data, 0, allData, 1, data.length);
		bh.send(data);
	}

}
