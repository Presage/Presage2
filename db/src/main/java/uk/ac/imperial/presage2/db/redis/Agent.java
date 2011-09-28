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

import java.util.Map;
import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgentFactory;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

public class Agent extends JedisPoolUser implements PersistentAgent {

	@Singleton
	static class Factory implements PersistentAgentFactory {

		private final StorageService db;
		private final JedisPool pool;

		@Inject
		Factory(StorageService db, JedisPool pool) {
			this.db = db;
			this.pool = pool;
		}

		@Override
		public PersistentAgent create(UUID id, String name) {
			if (db.getSimulation() == null) {
				throw new RuntimeException("Simulation has not been set!");
			}
			final Jedis r = pool.getResource();
			final long simID = db.getSimulation().getID();
			try {
				Keys.Agent keyGen = new Keys.Agent(simID, id);
				PersistentAgent agent = new Agent(simID, id, this.db, this.pool);

				r.sadd(Keys.Simulation.agentsSet(simID), id.toString());
				r.set(keyGen.name(), name);
				return agent;
			} finally {
				pool.returnResource(r);
			}
		}

		@Override
		public PersistentAgent get(PersistentSimulation sim, UUID id) {
			final Jedis r = pool.getResource();
			try {
				if (r.sismember(Keys.Simulation.agentsSet(sim.getID()), id.toString()))
					return new Agent(sim.getID(), id, this.db, this.pool);
				else
					return null;
			} finally {
				pool.returnResource(r);
			}
		}

	}

	private final long simulationID;
	private final UUID agentID;
	private final Keys.Agent key;

	Agent(final long simulationID, final UUID agentID, StorageService db, final JedisPool pool) {
		super(db, pool);
		this.simulationID = simulationID;
		this.agentID = agentID;
		this.key = new Keys.Agent(this.simulationID, this.agentID);
	}

	@Override
	public UUID getID() {
		return this.agentID;
	}

	@Override
	public String getName() {
		return getString(this.key.name());
	}

	@Override
	public void setRegisteredAt(int time) {
		setInt(this.key.registeredAt(), time);
	}

	@Override
	public void setDeRegisteredAt(int time) {
		setInt(this.key.deregisteredAt(), time);
	}

	@Override
	public Object getProperty(String key) {
		return getString(this.key.property(key));
	}

	@Override
	public void setProperty(String key, Object value) {
		setString(this.key.property(key), value.toString());
	}

	@Override
	public void createRelationshipTo(PersistentAgent p, String type, Map<String, Object> parameters) {
		throw new UnsupportedOperationException(
				"createRelationshipTo not implemented for redis yet.");
	}

	@Override
	public TransientAgentState getState(int time) {
		return new AgentState(simulationID, getID(), time, this.db, this.pool);
	}

}
