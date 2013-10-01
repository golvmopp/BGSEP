import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;


public class Client implements Runnable, Observer{

	private final String SERVERIP = "192.168.1.8";
	private final int SERVERPORT = 5432;
	private DatagramPacket packet;
	private DatagramSocket socket;
	private String message;
	
	public Client()
	{
		try {
			socket = new DatagramSocket();
			message = "Default message";
			InetAddress serverAddress;
			serverAddress = InetAddress.getByName(SERVERIP);
			packet = new DatagramPacket(message.getBytes(), 
					message.length(), serverAddress, SERVERPORT);
			
			
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
			while(true)
			{
				try {
					socket.send(packet);
					Thread.sleep(10000);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
	}

	@Override
	public void update(Observable o, Object obj) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(KeyEvent.VK_C);
		packet.setData(b.array());
		packet.setLength(b.array().length);
	}
	
}
