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
package uk.ac.imperial.presage2.db.redis;

import java.util.UUID;

import redis.clients.jedis.JedisPool;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

public class AgentState extends JedisPoolUser implements TransientAgentState {

	final UUID agentID;
	final int time;
	final Keys.AgentState key;

	AgentState(UUID agentID, int time, StorageService db, JedisPool pool) {
		super(db, pool);
		this.agentID = agentID;
		this.time = time;
		this.key = new Keys.AgentState(db.getSimulation().getID(), this.agentID, this.time);
	}

	@Override
	public int getTime() {
		return this.time;
	}

	@Override
	public PersistentAgent getAgent() {
		return db.getAgent(agentID);
	}

	@Override
	public Object getProperty(String key) {
		return getString(this.key.property(key));
	}

	@Override
	public void setProperty(String key, Object value) {
		setString(this.key.property(key), value.toString());
	}

}
