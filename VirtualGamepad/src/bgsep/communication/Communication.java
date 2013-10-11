/*
   Copyright (C) 2013  Patrik Wållgren

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

import java.util.Observable;
import java.util.Observer;

import bgsep.model.Button;
import bgsep.model.Gyro;
import bgsep.model.JoystickHandler;

/**
 * description...
 * @author Patrik Wållgren
 *
 */
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
		} else if((o instanceof JoystickHandler || o instanceof Gyro) && obj instanceof CommunicationNotifier) {
			CommunicationNotifier cn = (CommunicationNotifier)obj;
			sender.send((byte)cn.id, cn.value);
		}
		
	}
	
	
	
	
}
