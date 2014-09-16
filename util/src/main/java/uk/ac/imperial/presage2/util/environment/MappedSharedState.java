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
package uk.ac.imperial.presage2.util.environment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.SharedState;
import uk.ac.imperial.presage2.core.environment.SharedStateAccessException;
import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.core.environment.StateTransformer;
import uk.ac.imperial.presage2.core.event.EventBus;

import com.google.inject.Inject;

public class MappedSharedState implements SharedStateStorage {

	private final Logger logger = Logger.getLogger(MappedSharedState.class);

	Map<String, Serializable> globalState;

	Map<UUID, Map<String, Serializable>> agentState;

	Queue<StateChange> stateChange;

	class StateChange {
		private final String key;
		private UUID participantId = null;
		private StateTransformer transformer = null;
		private Serializable value = null;

		StateChange(String key, StateTransformer transformer) {
			super();
			this.key = key;
			this.transformer = transformer;
		}

		StateChange(String key, Serializable value) {
			super();
			this.key = key;
			this.value = value;
		}

		StateChange(String key, UUID participantId, StateTransformer transformer) {
			super();
			this.key = key;
			this.participantId = participantId;
			this.transformer = transformer;
		}

		StateChange(String key, UUID participantId, Serializable value) {
			super();
			this.key = key;
			this.participantId = participantId;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public UUID getParticipantId() {
			return participantId;
		}

		Serializable getChange(EnvironmentSharedStateAccess state) {
			if (this.transformer != null) {
				if (this.participantId == null)
					return this.transformer.transform(state.getGlobal(this.key));
				else
					return this.transformer.transform(state.get(this.key, this.participantId));
			} else {
				return value;
			}
		}
	}

	public MappedSharedState() {
		super();
		globalState = initGlobalStateMap();
		agentState = initAgentStateMap();
		stateChange = new ConcurrentLinkedQueue<MappedSharedState.StateChange>();
	}

	@Inject(optional = true)
	public void setEventBus(EventBus e) {
		e.subscribe(this);
	}

	protected Map<String, Serializable> initGlobalStateMap() {
		return new HashMap<String, Serializable>();
	}

	@SuppressWarnings("serial")
	protected Map<UUID, Map<String, Serializable>> initAgentStateMap() {
		// extended HashMap which adds an element if we request a UUID we don't
		// have an element for yet.
		return new HashMap<UUID, Map<String, Serializable>>() {
			@Override
			public Map<String, Serializable> get(Object key) {
				Map<String, Serializable> agent = super.get(key);
				if (agent == null && key instanceof UUID) {
					agent = new HashMap<String, Serializable>();
					this.put((UUID) key, agent);
				}
				return agent;
			}
		};
	}

	@Override
	public Serializable getGlobal(String name) {
		return globalState.get(name);
	}

	@Override
	public void changeGlobal(String name, StateTransformer change) {
		stateChange.add(new StateChange(name, change));
	}

	@Override
	public void changeGlobal(String name, final Serializable value) {
		stateChange.add(new StateChange(name, value));
	}

	@Override
	public void createGlobal(SharedState state) {
		createGlobal(state.getName(), state.getValue());
	}

	@Override
	public synchronized void createGlobal(String name, Serializable value) {
		if (!globalState.containsKey(name)) {
			globalState.put(name, value);
		} else
			throw new SharedStateAccessException("Cannot create global '" + name
					+ "': already exists.");
	}

	@Override
	public synchronized void deleteGlobal(String name) {
		globalState.remove(name);
	}

	@Override
	public Serializable get(String name, UUID participantID) {
		Map<String, Serializable> state = agentState.get(participantID);
		if (state != null) {
			return state.get(name);
		} else {
			return null;
		}
	}

	@Override
	public void change(String name, UUID participantID, StateTransformer change) {
		stateChange.add(new StateChange(name, participantID, change));
	}

	@Override
	public void change(String name, UUID participantID, final Serializable value) {
		stateChange.add(new StateChange(name, participantID, value));
	}

	@Override
	public void create(ParticipantSharedState state) {
		create(state.getName(), state.getParticipantID(), state.getValue());
	}

	@Override
	public synchronized void create(String name, UUID participantID, Serializable value) {
		Map<String, Serializable> agent = agentState.get(participantID);
		if (!agent.containsKey(name)) {
			agent.put(name, value);
		} else {
			throw new SharedStateAccessException("Cannot create state '" + name + "' for agent '"
					+ participantID + "': already exists");
		}
	}

	@Override
	public void delete(String name, UUID participantID) {
		agentState.get(participantID).remove(name);
	}

	@Override
	public void incrementTime() {
		updateState();
	}

	protected void updateState() {
		logger.info("Updating state.");
		while (this.stateChange.peek() != null) {
			StateChange c = stateChange.poll();
			if (c.getParticipantId() == null)
				globalState.put(c.getKey(), c.getChange(this));
			else {
				Map<String, Serializable> agent = agentState.get(c.getParticipantId());
				if (agent == null) {
					agent = new HashMap<String, Serializable>();
					agentState.put(c.getParticipantId(), agent);
				}
				agent.put(c.getKey(), c.getChange(this));
			}
		}
	}

}
