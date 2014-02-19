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
package uk.ac.imperial.presage2.core.db;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.inject.Singleton;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

/**
 * This a stub implementation of the StorageService interface. It is intended to
 * be the fallback implementation when a database is not defined such that code
 * that worked with a database does not throw NullPointerExceptions when run
 * without.
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
class StubStorageService implements StorageService {

	StubSimulation current = null;

	@Override
	public PersistentSimulation createSimulation(String name, String classname,
			String state, int finishTime) {
		current = new StubSimulation(name, classname, state, finishTime);
		return current;
	}

	@Override
	public PersistentSimulation getSimulation() {
		return current;
	}

	@Override
	public PersistentSimulation getSimulationById(long id) {
		return null;
	}

	@Override
	public List<Long> getSimulations() {
		return Collections.emptyList();
	}

	@Override
	public void setSimulation(PersistentSimulation sim) {
	}

	@Override
	public PersistentAgent createAgent(UUID agentID, String name) {
		return new StubAgent(agentID);
	}

	@Override
	public PersistentAgent getAgent(UUID agentID) {
		return new StubAgent(agentID);
	}

	@Override
	public TransientAgentState getAgentState(UUID agentID, int time) {
		return new StubAgent(agentID);
	}

	class StubSimulation implements PersistentSimulation, PersistentEnvironment {

		public StubSimulation(String name, String classname, String state,
				int finishTime) {
		}

		@Override
		public long getID() {
			return 0;
		}

		@Override
		public void addParameter(String name, String value) {
		}

		@Override
		public Map<String, String> getParameters() {
			return Collections.emptyMap();
		}

		@Override
		public int getFinishTime() {
			return 0;
		}

		@Override
		public void setCurrentTime(int time) {
		}

		@Override
		public int getCurrentTime() {
			return 0;
		}

		@Override
		public void setState(String newState) {
		}

		@Override
		public String getState() {
			return null;
		}

		@Override
		public PersistentEnvironment getEnvironment() {
			return this;
		}

		@Override
		public void setParentSimulation(PersistentSimulation parent) {
		}

		@Override
		public PersistentSimulation getParentSimulation() {
			return null;
		}

		@Override
		public List<Long> getChildren() {
			return Collections.emptyList();
		}

		@Override
		public void setFinishedAt(long time) {
		}

		@Override
		public long getFinishedAt() {
			return 0;
		}

		@Override
		public void setStartedAt(long time) {
		}

		@Override
		public long getStartedAt() {
			return 0;
		}

		@Override
		public long getCreatedAt() {
			return 0;
		}

		@Override
		public String getClassName() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Set<PersistentAgent> getAgents() {
			return Collections.emptySet();
		}

		@Override
		public Map<String, String> getProperties() {
			return Collections.emptyMap();
		}

		@Override
		public String getProperty(String key) {
			return null;
		}

		@Override
		public void setProperty(String key, String value) {
		}

		@Override
		public Map<String, String> getProperties(int timestep) {
			return Collections.emptyMap();
		}

		@Override
		public String getProperty(String key, int timestep) {
			return null;
		}

		@Override
		public void setProperty(String key, int timestep, String value) {
		}
	}

	class StubAgent implements PersistentAgent, TransientAgentState {

		public StubAgent(UUID agentID) {
		}

		@Override
		public UUID getID() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public void setRegisteredAt(int time) {
		}

		@Override
		public void setDeRegisteredAt(int time) {
		}

		@Override
		public Map<String, String> getProperties() {
			return Collections.emptyMap();
		}

		@Override
		public String getProperty(String key) {
			return null;
		}

		@Override
		public void setProperty(String key, String value) {

		}

		@Override
		public TransientAgentState getState(int time) {
			return this;
		}

		@Override
		public int getTime() {
			return 0;
		}

		@Override
		public PersistentAgent getAgent() {
			return this;
		}
	}

}
