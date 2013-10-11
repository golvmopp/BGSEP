/*
   Copyright (C) 2013 Linus Lindgren

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
	 * Returns the instance of the {@link IdHandler}. If the parameter doesn't
	 * equals the already existing number of IDs, the instance will be
	 * reinstantiated.
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
