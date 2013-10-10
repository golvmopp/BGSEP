package util;

import java.util.HashMap;

/**
 * 
 * The IdHandler contains a number of ID's which is either occupied or
 * unoccupied.
 * 
 * @author Linus Lindgren (linlind@student.chalmers.se)
 * 
 * 
 * 
 */
public class IdHandler {

	private HashMap<Integer, Boolean> ids;
	private int numberOfIds;

	private static IdHandler instance;

	private IdHandler(int numberOfIds) {
		this.numberOfIds = numberOfIds;
		ids = new HashMap<Integer, Boolean>();

		for (int i = 0; i < numberOfIds; i++) {
			ids.put(i, false);
		}
	}

	/**
	 * Returns the instance of the {@link IdHandler}. If the parameter doesn't equals
	 * the already existing number of IDs, the instance will be reinstantiated.
	 * 
	 * @param numberOfIds
	 *            The number of IDs the instance will hold.
	 * @return The instance of the {@link IdHandler}
	 */
	public static IdHandler getInstance(int numberOfIds) {
		if (instance == null || instance.getNumberOfIds() != numberOfIds) {
			instance = new IdHandler(numberOfIds);
		}
		return instance;
	}

	/**
	 * 
	 * @return Returns the first unoccupied ID that is found. If every ID is
	 *         occupied -1 is returned.
	 */
	public int getUnoccupiedId() {
		int unoccupiedId = -1;
		for (int id : ids.keySet()) {
			if (!ids.get(id)) {
				unoccupiedId = id;
				ids.put(id, true);
				break;
			}
		}
		return unoccupiedId;
	}

	/**
	 * Sets an ID to unoccupied and will be free to get.
	 * 
	 * @param id
	 *            The ID that will be unoccupied
	 */
	public void setIdUnoccupied(int id) {
		if (ids.containsKey(id))
			ids.put(id, false);
	}

	private int getNumberOfIds() {
		return numberOfIds;
	}
}
