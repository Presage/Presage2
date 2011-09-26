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

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.Transaction;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgentFactory;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.SimulationFactory;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class RedisDatabase implements DatabaseService, StorageService, Provider<JedisPool> {

	private final Logger logger = Logger.getLogger(RedisDatabase.class);
	private final String host;
	private final JedisPoolConfig config;

	private JedisPool pool = null;

	Simulation.Factory simFactory;
	PersistentAgentFactory agentFactory;

	PersistentSimulation simulation;

	@Inject
	RedisDatabase(@Named("redis.host") String host, JedisPoolConfig config) {
		super();
		this.host = host;
		this.config = config;
	}

	@Override
	public SimulationFactory getSimulationFactory() {
		return simFactory;
	}

	@Override
	public PersistentSimulation createSimulation(String name, String classname, String state,
			int finishTime) {
		PersistentSimulation sim = simFactory.create(name, classname, state, finishTime);
		this.setSimulation(sim);
		return sim;
	}

	@Override
	public PersistentSimulation getSimulation() {
		return this.simulation;
	}

	@Override
	public void setSimulation(PersistentSimulation sim) {
		this.simulation = sim;
	}

	@Override
	public PersistentAgent createAgent(UUID agentID, String name) {
		return agentFactory.create(agentID, name);
	}

	@Override
	public PersistentAgent getAgent(UUID agentID) {
		return this.agentFactory.get(getSimulation(), agentID);
	}

	@Override
	public TransientAgentState getAgentState(UUID agentID, int time) {
		return new AgentState(agentID, time, this, get());
	}

	@Override
	public Transaction startTransaction() {
		return new MockTransaction();
	}

	@Override
	public void start() throws Exception {
		if (!isStarted()) {
			this.pool = new JedisPool(this.config, this.host);

			this.simFactory = new Simulation.Factory(this, this.pool);
			this.agentFactory = new Agent.Factory(this, this.pool);
		}
	}

	@Override
	public boolean isStarted() {
		return pool != null;
	}

	@Override
	public void stop() {
		pool.destroy();
	}

	@Override
	public JedisPool get() {
		try {
			this.start();
		} catch (Exception e) {
			logger.warn("Exception thrown when starting db", e);
		}
		return pool;
	}

	class MockTransaction implements Transaction {
		@Override
		public void failure() {
		}

		@Override
		public void finish() {
		}

		@Override
		public void success() {
		}
	}

	@Override
	public PersistentSimulation getSimulationById(long id) {
		return simFactory.get(id);
	}

	@Override
	public List<Long> getSimulations() {
		return simFactory.getIds();
	}

}
