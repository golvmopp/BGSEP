package bgsep.wifi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

import android.os.AsyncTask;


public class Client implements Observer{

	private final String SERVERIP = "192.168.1.8";
	private final int SERVERPORT = 5432;
	private DatagramSocket socket;
	public Client()
	{
		try {
			socket = new DatagramSocket();
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void update(Observable o, Object obj) {
		
		if(obj instanceof ByteBuffer) {
			Communicate comm = new Communicate();
			comm.execute((ByteBuffer)obj);
		}
	}
	
	private class Communicate extends AsyncTask<ByteBuffer, Void, Void> {

		@Override
		protected Void doInBackground(ByteBuffer... params) {
			for(ByteBuffer msg : params) {
				try {
					InetAddress serverAddress = InetAddress.getByName(SERVERIP);
					DatagramPacket packet = new DatagramPacket(msg.array(), 
							msg.array().length, serverAddress, SERVERPORT);
					
					socket.send(packet);
					
				} 
				catch (UnknownHostException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
}
