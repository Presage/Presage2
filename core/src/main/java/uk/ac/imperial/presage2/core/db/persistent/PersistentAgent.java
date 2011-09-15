/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.core.db.persistent;

import java.util.Map;
import java.util.UUID;

/**
 * Persistent store for an agent.
 * @author Sam Macbeth
 *
 */
public interface PersistentAgent {

	UUID getID();

	String getName();

	void setRegisteredAt(int time);

	void setDeRegisteredAt(int time);

	/**
	 * Get a property associated with this agent.
	 * @param key
	 * @return
	 */
	Object getProperty(String key);

	/**
	 * Set a property associated with this agent.
	 * @param key
	 * @param value
	 */
	void setProperty(String key, Object value);

	/**
	 * Create a relationship to another agent.
	 * @param p
	 * @param type
	 * @param parameters
	 */
	void createRelationshipTo(PersistentAgent p, String type,
			Map<String, Object> parameters);

}
