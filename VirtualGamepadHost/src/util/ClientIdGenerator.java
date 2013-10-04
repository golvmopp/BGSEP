package util;

import java.util.HashMap;

public class ClientIdGenerator {
	private HashMap<Integer, Boolean> ids;
	private static ClientIdGenerator instance;

	private ClientIdGenerator() {
		ids = new HashMap<Integer, Boolean>();
		ids.put(0, false);
		ids.put(1, false);
		ids.put(2, false);
		ids.put(3, false);
		ids.put(4, false);
	}

	public static ClientIdGenerator getInstance() {
		if (instance == null) {
			instance = new ClientIdGenerator();
		}
		return instance;
	}

	public int getGeneratedId() {
		int generatedId = -1;
		for (int id : ids.keySet()) {
			if (!ids.get(id)) {
				generatedId = id;
				ids.put(id, true);
				break;
			}
		}
		return generatedId;
	}

	public void removeClient(int id) {
		if (ids.containsKey(id))
			ids.put(id, false);
	}
}
