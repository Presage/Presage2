/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.db.sql;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;

public class Environment implements PersistentEnvironment {

	final SqlStorage sto;

	boolean pdirty = false;
	boolean tdirty = false;

	public final long simId;

	public Map<String, String> properties = new ConcurrentHashMap<String, String>();
	public Map<Integer, Map<String, String>> transientProperties = new ConcurrentHashMap<Integer, Map<String, String>>();

	Environment(long simId, SqlStorage sto) {
		super();
		this.simId = simId;
		this.sto = sto;
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public synchronized String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public synchronized void setProperty(String key, String value) {
		properties.put(key, value);
		this.sto.environmentQ.add(this);
	}

	@Override
	public synchronized Map<String, String> getProperties(int timestep) {
		if (!transientProperties.containsKey(timestep)) {
			return Collections.emptyMap();
		}
		return transientProperties.get(timestep);
	}

	@Override
	public synchronized String getProperty(String key, int timestep) {
		if (!transientProperties.containsKey(timestep)) {
			return null;
		} else {
			if (!transientProperties.get(timestep).containsKey(key))
				return null;
			else
				return transientProperties.get(timestep).get(key);
		}
	}

	@Override
	public synchronized void setProperty(String key, int timestep, String value) {
		synchronized (sto.environmentTransientQ) {
			if (!transientProperties.containsKey(timestep)) {
				transientProperties.put(timestep,
						new ConcurrentHashMap<String, String>());
			}
			transientProperties.get(timestep).put(key, value);
			this.sto.environmentTransientQ.add(this);
		}
	}

}
