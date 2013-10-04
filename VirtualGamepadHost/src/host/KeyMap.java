package host;

import java.util.ArrayList;
import java.util.HashMap;

public class KeyMap {
	private ArrayList<Integer> keyCodes;
	
	private static final int KEYS_PER_CLIENT = 20;
	
	private static final int MY_COMPUTER = 182;
	private static final int MY_CALCULATOR = 183;
	private static final int NUM_LOCK = 144;
	private static final int SCROLL_LOCK = 145;
	
	public KeyMap() {
		keyCodes = new ArrayList<Integer>();
		
		for (int i = 124; i <= 149; i++) {
			if (i != MY_COMPUTER && i != MY_CALCULATOR && i != NUM_LOCK && i != SCROLL_LOCK) { 
				keyCodes.add(i);
			}
		}
	}
	
	public int getKeyCode(int clientID, int buttonID) {
		int index = clientID * KEYS_PER_CLIENT + buttonID;
		if (index < keyCodes.size()) {
			return keyCodes.get(clientID * KEYS_PER_CLIENT + buttonID);
		} else {
			throw new IllegalArgumentException("clientID " + clientID + " is too big!");
		}
		
	}
}