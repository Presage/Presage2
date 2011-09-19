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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.SimulationFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

public class Simulation extends JedisPoolUser implements PersistentSimulation {

	private final long simulationID;

	@Singleton
	static class Factory implements SimulationFactory {

		private final StorageService db;
		private final JedisPool pool;

		@Inject
		Factory(StorageService db, JedisPool jedis) {
			this.db = db;
			this.pool = jedis;
		}

		@Override
		public PersistentSimulation create(String name, String classname, String state,
				int finishTime) {
			final Jedis jedis = pool.getResource();
			Long simID;
			PersistentSimulation psim = null;
			try {
				if (!jedis.exists(Keys.Simulation.ID_COUNTER)) {
					jedis.setnx(Keys.Simulation.ID_COUNTER, Long.valueOf(0).toString());
				}
				// get unique key
				simID = jedis.incr(Keys.Simulation.ID_COUNTER);
				psim = new Simulation(simID, this.db, this.pool);

				jedis.lpush(Keys.Simulation.ID_SET, simID.toString());

				// readonly fields manually set
				jedis.set(Keys.Simulation.name(simID), name);
				jedis.set(Keys.Simulation.className(simID), classname);
				jedis.set(Keys.Simulation.finishTime(simID), Integer.valueOf(finishTime).toString());
				jedis.set(Keys.Simulation.createdAt(simID), Long.valueOf(new Date().getTime())
						.toString());

				// others can use Simulation's setters
				psim.setState(state);
				psim.setCurrentTime(0);

			} finally {
				pool.returnResource(jedis);
			}
			return psim;
		}

		@Override
		public PersistentSimulation get(long simulationID) {
			final Jedis jedis = pool.getResource();
			PersistentSimulation sim = null;
			try {
				if (jedis.sismember(Keys.Simulation.ID_SET, Long.valueOf(simulationID).toString()))
					sim = new Simulation(simulationID, this.db, this.pool);
			} finally {
				pool.returnResource(jedis);
			}
			return sim;
		}

		public List<Long> getIds() {
			final Jedis jedis = pool.getResource();
			List<Long> ids = new LinkedList<Long>();
			try {
				for (String s : jedis.lrange(Keys.Simulation.ID_SET, 0,
						jedis.llen(Keys.Simulation.ID_SET))) {
					ids.add(Long.parseLong(s));
				}
			} finally {
				pool.returnResource(jedis);
			}
			return ids;
		}

	}

	Simulation(long simulationID, StorageService db, JedisPool pool) {
		super(db, pool);
		this.simulationID = simulationID;
	}

	@Override
	public void addParameter(String name, Object value) {
		final Jedis jedis = pool.getResource();
		try {
			jedis.sadd(Keys.Simulation.PARAMETER_NAMES, name);
			jedis.sadd(Keys.Simulation.parametersSet(simulationID), name);
			jedis.set(Keys.Simulation.parameterValue(simulationID, name), value.toString());
		} finally {
			pool.returnResource(jedis);
		}
	}

	@Override
	public Map<String, Object> getParameters() {
		final Jedis jedis = pool.getResource();
		final Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			final Set<String> paramNames = jedis.smembers(Keys.Simulation
					.parametersSet(simulationID));
			for (String p : paramNames) {
				parameters.put(p, jedis.get(Keys.Simulation.parameterValue(simulationID, p)));
			}
		} finally {
			pool.returnResource(jedis);
		}
		return parameters;
	}

	@Override
	public int getFinishTime() {
		return getInt(Keys.Simulation.finishTime(simulationID));
	}

	@Override
	public void setCurrentTime(int time) {
		setInt(Keys.Simulation.currentTime(simulationID), time);
	}

	@Override
	public int getCurrentTime() {
		return getInt(Keys.Simulation.currentTime(simulationID));
	}

	@Override
	public void setState(String newState) {
		final Jedis r = pool.getResource();
		try {
			// remove previous state association
			final String prevState = getState();
			if (prevState != null) {
				r.srem(Keys.Simulation.stateMembershipSet(prevState), Long.valueOf(simulationID)
						.toString());
			}
			// add new state association
			r.set(Keys.Simulation.state(simulationID), newState);
			r.sadd(Keys.Simulation.STATE_NAMES, newState);
			r.sadd(Keys.Simulation.stateMembershipSet(newState), Long.valueOf(simulationID)
					.toString());
		} finally {
			pool.returnResource(r);
		}
	}

	@Override
	public String getState() {
		return getString(Keys.Simulation.state(simulationID));
	}

	@Override
	public void addChild(PersistentSimulation child) {
		throw new UnsupportedOperationException(
				"PersistentSimulation.addChild() not implemented for redis db.");
	}

	@Override
	public void setParentSimulation(PersistentSimulation parent) {
		throw new UnsupportedOperationException(
				"PersistentSimulation.setParentSimulation() not implemented for redis db.");
	}

	@Override
	public PersistentSimulation getParentSimulation() {
		throw new UnsupportedOperationException(
				"PersistentSimulation.getParentSimulation() not implemented for redis db.");
	}

	@Override
	public void setFinishedAt(long time) {
		setLong(Keys.Simulation.finishTime(simulationID), time);
	}

	@Override
	public long getFinishedAt() {
		return getLong(Keys.Simulation.finishTime(simulationID));
	}

	@Override
	public void setStartedAt(long time) {
		setLong(Keys.Simulation.startedAt(simulationID), time);
	}

	@Override
	public long getStartedAt() {
		return getLong(Keys.Simulation.startedAt(simulationID));
	}

	@Override
	public long getCreatedAt() {
		return getLong(Keys.Simulation.createdAt(simulationID));
	}

	@Override
	public String getClassName() {
		return getString(Keys.Simulation.className(simulationID));
	}

	@Override
	public String getName() {
		return getString(Keys.Simulation.name(simulationID));
	}

	@Override
	public long getID() {
		return this.simulationID;
	}

	@Override
	public Set<PersistentAgent> getAgents() {
		final Jedis r = pool.getResource();
		Set<PersistentAgent> agents = new HashSet<PersistentAgent>();
		try {
			for (String aid : r.smembers(Keys.Simulation.agentsSet(getID()))) {
				agents.add(new Agent(getID(), UUID.fromString(aid), this.db, this.pool));
			}
		} finally {
			pool.returnResource(r);
		}
		return agents;
	}

}
