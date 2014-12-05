/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.core.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

public abstract class TupleStorageService implements StorageService {

	public static class KEYS {
		public final static String name = "name";
		public final static String classname = "classname";
		public final static String state = "state";
		public final static String finishTime = "finishTime";
		public final static String time = "t";
		public final static String startedAt = "startedAt";
		public final static String finishedAt = "finishedAt";
	}

	protected final String[] reservedKeys = { KEYS.name, KEYS.classname, KEYS.state,
			KEYS.time, KEYS.startedAt, KEYS.finishedAt };
	protected final String[] reservedAgentKeys = { "name" };

	PersistentSimulation current = null;

	@Override
	public PersistentSimulation createSimulation(String name, String classname,
			String state, int finishTime) {
		long id = getNextId();
		storeTuple(id, KEYS.name, name);
		storeTuple(id, KEYS.classname, classname);
		storeTuple(id, KEYS.state, state);
		storeTuple(id, KEYS.time, 0);
		storeParameter(id, KEYS.finishTime, Integer.toString(finishTime));
		return new Simulation(id);
	}

	@Override
	public PersistentSimulation getSimulationById(long id) {
		if (new HashSet<Long>(getSimulations()).contains(id))
			return new Simulation(id);
		else
			return null;
	}

	@Override
	public PersistentSimulation getSimulation() {
		return current;
	}

	@Override
	public PersistentSimulation get() {
		return getSimulation();
	}

	@Override
	public void setSimulation(PersistentSimulation sim) {
		this.current = sim;
	}

	@Override
	public PersistentAgent createAgent(UUID agentID, String name) {
		long simId = current.getID();
		storeTuple(simId, KEYS.name, agentID, name);
		return new Agent(simId, agentID);
	}

	@Override
	public PersistentAgent getAgent(UUID agentID) {
		long simId = current.getID();
		return new Agent(simId, agentID);
	}

	@Override
	public TransientAgentState getAgentState(UUID agentID, int time) {
		return getAgent(agentID).getState(time);
	}

	protected abstract long getNextId();

	protected abstract void storeParameter(long id, String key, String value);

	protected abstract void storeTuple(long id, String key, String value);

	protected abstract void storeTuple(long id, String key, int value);

	protected abstract void storeTuple(long id, String key, double value);

	protected abstract void storeTuple(long id, String key, int t, String value);

	protected abstract void storeTuple(long id, String key, int t, int value);

	protected abstract void storeTuple(long id, String key, int t, double value);

	protected abstract void storeTuple(long id, String key, UUID agent,
			String value);

	protected abstract void storeTuple(long id, String key, UUID agent,
			int value);

	protected abstract void storeTuple(long id, String key, UUID agent,
			double value);

	protected abstract void storeTuple(long id, String key, UUID agent, int t,
			String value);

	protected abstract void storeTuple(long id, String key, UUID agent, int t,
			int value);

	protected abstract void storeTuple(long id, String key, UUID agent, int t,
			double value);

	protected abstract Set<String> fetchParameterKeys(long id);

	protected abstract String fetchParameter(long id, String key);

	protected String fetchTuple(long id, String key) {
		return fetchTuple(id, key, String.class);
	}

	protected abstract <T> T fetchTuple(long id, String key, Class<T> type);

	protected String fetchTuple(long id, String key, int t) {
		return fetchTuple(id, key, t, String.class);
	}

	protected abstract <T> T fetchTuple(long id, String key, int t,
			Class<T> type);

	protected String fetchTuple(long id, String key, UUID agent) {
		return fetchTuple(id, key, agent, String.class);
	}

	protected abstract <T> T fetchTuple(long id, String key, UUID agent,
			Class<T> type);

	protected String fetchTuple(long id, String key, UUID agent, int t) {
		return fetchTuple(id, key, agent, t, String.class);
	}

	protected abstract <T> T fetchTuple(long id, String key, UUID agent, int t,
			Class<T> type);

	protected class Simulation implements PersistentSimulation, PersistentEnvironment {

		final long id;

		public Simulation(long id) {
			super();
			this.id = id;
		}

		@Override
		public long getID() {
			return id;
		}

		@Override
		public void addParameter(String name, String value) {
			storeParameter(id, name, value);
		}

		@Override
		public Map<String, String> getParameters() {
			Map<String, String> params = new HashMap<String, String>();
			for (String key : fetchParameterKeys(id)) {
				params.put(key, fetchParameter(id, key));
			}
			return params;
		}

		@Override
		public void setCurrentTime(int time) {
			storeTuple(KEYS.time, time);
		}

		@Override
		public int getCurrentTime() {
			return fetchTuple(KEYS.time, Integer.class);
		}

		@Override
		public void setState(String newState) {
			storeTuple(KEYS.state, newState);
		}

		@Override
		public String getState() {
			return fetchTuple(KEYS.state);
		}

		@Override
		public void setFinishedAt(long time) {
			storeTuple(KEYS.finishedAt, time);
		}

		@Override
		public long getFinishedAt() {
			return fetchTuple(KEYS.finishedAt, Long.class);
		}

		@Override
		public void setStartedAt(long time) {
			storeTuple(KEYS.startedAt, time);
		}

		@Override
		public long getStartedAt() {
			return fetchTuple(KEYS.startedAt, Long.class);
		}

		@Override
		public String getClassName() {
			return fetchTuple(KEYS.classname);
		}

		@Override
		public String getName() {
			return fetchTuple(KEYS.name);
		}

		@Override
		public int getFinishTime() {
			return Integer.parseInt(fetchParameter(id, KEYS.finishTime));
		}

		@Override
		public void storeTuple(String key, String value) {
			TupleStorageService.this.storeTuple(id, key, value);
		}

		@Override
		public void storeTuple(String key, int t, String value) {
			TupleStorageService.this.storeTuple(id, key, t, value);
		}

		@Override
		public void storeTuple(String key, UUID agent, String value) {
			TupleStorageService.this.storeTuple(id, key, agent, value);
		}

		@Override
		public void storeTuple(String key, UUID agent, int t, String value) {
			TupleStorageService.this.storeTuple(id, key, agent, t, value);
		}

		@Override
		public String fetchTuple(String key) {
			return TupleStorageService.this.fetchTuple(id, key);
		}

		@Override
		public <T> T fetchTuple(String key, Class<T> type) {
			return TupleStorageService.this.fetchTuple(id, key, type);
		}

		@Override
		public String fetchTuple(String key, int t) {
			return TupleStorageService.this.fetchTuple(id, key, t);
		}

		@Override
		public <T> T fetchTuple(String key, int t, Class<T> type) {
			return TupleStorageService.this.fetchTuple(id, key, t, type);
		}

		@Override
		public String fetchTuple(String key, UUID agent) {
			return TupleStorageService.this.fetchTuple(id, key, agent);
		}

		@Override
		public <T> T fetchTuple(String key, UUID agent, Class<T> type) {
			return TupleStorageService.this.fetchTuple(id, key, agent, type);
		}

		@Override
		public String fetchTuple(String key, UUID agent, int t) {
			return TupleStorageService.this.fetchTuple(id, key, agent, t);
		}

		@Override
		public <T> T fetchTuple(String key, UUID agent, int t, Class<T> type) {
			return TupleStorageService.this.fetchTuple(id, key, agent, t, type);
		}

		@Override
		public void storeTuple(String key, int value) {
			TupleStorageService.this.storeTuple(id, key, value);
		}

		@Override
		public void storeTuple(String key, double value) {
			TupleStorageService.this.storeTuple(id, key, value);
		}

		@Override
		public void storeTuple(String key, int t, int value) {
			TupleStorageService.this.storeTuple(id, key, t, value);
		}

		@Override
		public void storeTuple(String key, int t, double value) {
			TupleStorageService.this.storeTuple(id, key, t, value);
		}

		@Override
		public void storeTuple(String key, UUID agent, int value) {
			TupleStorageService.this.storeTuple(id, key, agent, value);
		}

		@Override
		public void storeTuple(String key, UUID agent, double value) {
			TupleStorageService.this.storeTuple(id, key, agent, value);
		}

		@Override
		public void storeTuple(String key, UUID agent, int t, int value) {
			TupleStorageService.this.storeTuple(id, key, agent, t, value);
		}

		@Override
		public void storeTuple(String key, UUID agent, int t, double value) {
			TupleStorageService.this.storeTuple(id, key, agent, t, value);
		}

		@Override
		public PersistentEnvironment getEnvironment() {
			return this;
		}

		@Override
		public Map<String, String> getProperties() {
			throw new UnsupportedOperationException(
					"TupleStorage does not support LIST");
		}

		@Override
		public String getProperty(String key) {
			return fetchTuple(key);
		}

		@Override
		public void setProperty(String key, String value) {
			storeTuple(key, value);
		}

		@Override
		public Map<String, String> getProperties(int timestep) {
			throw new UnsupportedOperationException(
					"TupleStorage does not support LIST");
		}

		@Override
		public String getProperty(String key, int timestep) {
			return fetchTuple(key, timestep);
		}

		@Override
		public void setProperty(String key, int timestep, String value) {
			storeTuple(key, timestep, value);
		}

	}

	protected class Agent implements PersistentAgent {

		final long simId;
		final UUID id;

		Agent(long simId, UUID id) {
			super();
			this.simId = simId;
			this.id = id;
		}

		@Override
		public UUID getID() {
			return id;
		}

		@Override
		public String getName() {
			return fetchTuple(simId, KEYS.name, id);
		}

		@Override
		public void setRegisteredAt(int time) {
			storeTuple(simId, "registeredAt", id, time);
		}

		@Override
		public void setDeRegisteredAt(int time) {
			storeTuple(simId, "deregisteredAt", id, time);
		}

		@Override
		public Map<String, String> getProperties() {
			throw new UnsupportedOperationException(
					"TupleStorage does not support LIST");
		}

		@Override
		public String getProperty(String key) {
			return fetchTuple(simId, key, id);
		}

		@Override
		public void setProperty(String key, String value) {
			storeTuple(simId, key, id, value);
		}

		@Override
		public TransientAgentState getState(int time) {
			return new TState(time);
		}

		@Override
		public String getProperty(String key, int t) {
			return fetchTuple(simId, key, id, t);
		}

		@Override
		public void setProperty(String key, int t, String value) {
			storeTuple(simId, key, id, t, value);
		}

		protected class TState implements TransientAgentState {

			final int t;

			TState(int t) {
				super();
				this.t = t;
			}

			@Override
			public int getTime() {
				return t;
			}

			@Override
			public PersistentAgent getAgent() {
				return Agent.this;
			}

			@Override
			public Map<String, String> getProperties() {
				throw new UnsupportedOperationException(
						"TupleStorage does not support LIST");
			}

			@Override
			public String getProperty(String key) {
				return Agent.this.getProperty(key, t);
			}

			@Override
			public void setProperty(String key, String value) {
				Agent.this.setProperty(key, t, value);
			}

		}

	}

}
