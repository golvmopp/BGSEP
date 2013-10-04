package bgsep.communication;

import java.util.Observable;
import java.util.Observer;

import bgsep.model.Button;
import bluetooth.Sender;


public class Communication implements Observer{
	
	private Sender sender;
	private static Communication comm;

	private Communication() {
		
	}
	
	public static Communication getInstance() {
		if(comm == null)
			comm = new Communication();
		
		return comm;
	}
	
	public void setSender(Sender sender) {
		this.sender = sender;
	}

	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Button) {
			Button button = (Button)o;
			sender.send((byte)button.getButtonID(), button.isPressed());
		}
		
	}
	
	
	
	
}
