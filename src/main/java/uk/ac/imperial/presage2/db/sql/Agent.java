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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

public class Agent implements PersistentAgent {

	final SqlStorage sto;

	boolean dirty = false;
	boolean transientDirty = false;

	public final long simId;

	UUID id;
	String name;
	public Map<String, String> properties = new HashMap<String, String>();
	public Map<Integer, Map<String, String>> transientProperties = new HashMap<Integer, Map<String, String>>();

	Agent(UUID id, String name, SqlStorage sto) {
		super();
		this.id = id;
		this.name = name;
		this.sto = sto;
		this.simId = sto.getSimId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public UUID getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setRegisteredAt(int time) {
	}

	@Override
	public void setDeRegisteredAt(int time) {
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
		this.sto.agentQ.add(this);
	}

	@Override
	public synchronized TransientAgentState getState(final int time) {
		if (!transientProperties.containsKey(time)) {
			transientProperties.put(time, new HashMap<String, String>());
		}
		final Map<String, String> state = transientProperties.get(time);

		return new TransientAgentState() {
			@Override
			public void setProperty(String key, String value) {
				state.put(key, value);
				sto.agentTransientQ.add(Agent.this);
			}

			@Override
			public int getTime() {
				return time;
			}

			@Override
			public String getProperty(String key) {
				return state.get(key);
			}

			@Override
			public Map<String, String> getProperties() {
				return state;
			}

			@Override
			public PersistentAgent getAgent() {
				return Agent.this;
			}
		};
	}

}
