package bgsep.communication;

import java.util.Observable;
import java.util.Observer;

import bgsep.model.Button;
import bgsep.model.JoystickHandler;


public class Communication implements Observer{
	
	private Sender sender;
	private static Communication instance = null;

	private Communication() {
		
	}
	
	public static synchronized Communication getInstance() {
		if(instance == null)
			instance = new Communication();
		
		return instance;
	}
	
	public void setSender(Sender sender) {
		this.sender = sender;
	}

	@Override
	public void update(Observable o, Object obj) {
		if(o instanceof Button) {
			Button button = (Button)o;
			sender.send((byte)button.getButtonID(), button.isPressed());
		} else if(o instanceof JoystickHandler && obj instanceof CommunicationNotifier) {
			CommunicationNotifier cn = (CommunicationNotifier)obj;
			sender.send((byte)cn.id, cn.value);
		}
		
	}
	
	
	
	
}
