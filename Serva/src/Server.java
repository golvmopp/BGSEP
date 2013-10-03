package src;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;


public class Server implements Runnable {

	private final int SERVERPORT = 5432;
	
	@Override
	public void run() {
		byte [] buf = new byte[32];
		
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		DatagramSocket socket = null;
		System.out.print("Server running\n");
		try
		{
			socket = new DatagramSocket(SERVERPORT);
			Robot robot = new Robot();
			boolean keyPress = false;
			
			while(true) {
			socket.receive(packet);
			
			//First bit indicates whether if it is a keyPress or a keyRelease
			if(buf[0] == 1) {
				keyPress = true;
				buf[0] = 0;
			}
			else
				keyPress = false;
			
			int message = ByteBuffer.wrap(buf).getInt();
			
			// Only press valid inputs
			if(message > 0 && message < 500)
			{
				if(keyPress)
					robot.keyPress(message);
				else
					robot.keyRelease(message);
			}
			buf = new byte[32];
			packet.setData(buf);
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}