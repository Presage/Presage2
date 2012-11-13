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
package uk.ac.imperial.presage2.rules;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.time.SessionPseudoClock;

import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.SharedState;
import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.core.environment.StateTransformer;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;
import uk.ac.imperial.presage2.core.simulator.Events;
import uk.ac.imperial.presage2.rules.facts.AgentStateTranslator;
import uk.ac.imperial.presage2.rules.facts.GenericAgentStateTranslator;
import uk.ac.imperial.presage2.rules.facts.GenericGlobalStateTranslator;
import uk.ac.imperial.presage2.rules.facts.StateTranslator;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Implementation of {@link SharedStateStorage} using a drools
 * {@link StatefulKnowledgeSession}.
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class RuleStorage implements SharedStateStorage,
		Provider<StatefulKnowledgeSession> {

	private final Logger logger = Logger.getLogger(RuleStorage.class);
	final private KnowledgeBase kbase;
	final private StatefulKnowledgeSession session;
	final private SessionPseudoClock clock;

	final private Vector<StateTranslator> globalTranslators = new Vector<StateTranslator>();
	final private Vector<AgentStateTranslator> agentTranslators = new Vector<AgentStateTranslator>();

	Map<String, FactHandle> globalState;

	Map<UUID, Map<String, FactHandle>> agentState;

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
					return this.transformer
							.transform(state.getGlobal(this.key));
				else
					return this.transformer.transform(state.get(this.key,
							this.participantId));
			} else {
				return value;
			}
		}
	}

	@Inject
	public RuleStorage(@Rules Map<Integer, String> ruleFiles) {
		super();
		SortedMap<Integer, String> sortedFiles = new TreeMap<Integer, String>(
				ruleFiles);
		Collection<String> ruleSets = sortedFiles.values();

		// drools initialisation
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();

		for (String path : ruleSets) {
			kbuilder.add(
					ResourceFactory.newClassPathResource(path, this.getClass()),
					ResourceType.DRL);
		}

		if (kbuilder.hasErrors()) {
			logger.fatal(kbuilder.getErrors().toString());
			System.exit(1);
		}

		KnowledgeBaseConfiguration baseConf = KnowledgeBaseFactory
				.newKnowledgeBaseConfiguration();
		baseConf.setOption(EventProcessingOption.STREAM);

		KnowledgeSessionConfiguration sessionConf = KnowledgeBaseFactory
				.newKnowledgeSessionConfiguration();
		sessionConf.setOption(ClockTypeOption.get("pseudo"));

		kbase = KnowledgeBaseFactory.newKnowledgeBase(baseConf);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

		session = kbase.newStatefulKnowledgeSession(sessionConf, null);
		clock = session.getSessionClock();
		// KnowledgeRuntimeLogger logger =
		// KnowledgeRuntimeLoggerFactory.newConsoleLogger(session);

		globalState = initGlobalStateMap();
		agentState = initAgentStateMap();
		stateChange = new ConcurrentLinkedQueue<StateChange>();
	}

	@Inject
	protected void initialiseTranslators(Set<StateTranslator> stateTranslators,
			Set<AgentStateTranslator> agentStateTranslators) {
		globalTranslators.clear();
		agentTranslators.clear();

		globalTranslators.addAll(stateTranslators);
		globalTranslators.add(new GenericGlobalStateTranslator());
		agentTranslators.addAll(agentStateTranslators);
		agentTranslators.add(new GenericAgentStateTranslator());
	}

	@Inject(optional = true)
	public void setEventBus(EventBus e) {
		e.subscribe(this);
	}

	protected Map<String, FactHandle> initGlobalStateMap() {
		return new HashMap<String, FactHandle>();
	}

	@SuppressWarnings("serial")
	protected Map<UUID, Map<String, FactHandle>> initAgentStateMap() {
		// extended HashMap which adds an element if we request a UUID we don't
		// have an element for yet.
		return new HashMap<UUID, Map<String, FactHandle>>() {
			@Override
			public Map<String, FactHandle> get(Object key) {
				Map<String, FactHandle> agent = super.get(key);
				if (agent == null && key instanceof UUID) {
					agent = new HashMap<String, FactHandle>();
					this.put((UUID) key, agent);
				}
				return agent;
			}
		};
	}

	private FactHandle getCachedHandle(String name) {
		return globalState.get(name);
	}

	private FactHandle getCachedHandle(String name, UUID participantID) {
		return agentState.get(participantID).get(name);
	}

	public KnowledgeBase getKbase() {
		return kbase;
	}

	@Override
	public Serializable getGlobal(String name) {
		for (StateTranslator t : globalTranslators) {
			if (t.canTranslate(name)) {
				FactHandle handle = getCachedHandle(name);
				if (handle == null)
					handle = session.getFactHandle(t.getFactObject(name, null));
				if (handle != null) {
					Object fact = session.getObject(handle);
					return t.getStateFromFact(fact);
				}
			}
		}
		return null;
	}

	@Override
	public void changeGlobal(String name, StateTransformer change) {
		stateChange.add(new StateChange(name, change));
	}

	@Override
	public void changeGlobal(String name, Serializable value) {
		stateChange.add(new StateChange(name, value));
	}

	@Override
	public void createGlobal(SharedState state) {
		createGlobal(state.getName(), state.getValue());
	}

	@Override
	public void createGlobal(String name, Serializable value) {
		for (StateTranslator t : globalTranslators) {
			if (t.canTranslate(name)) {
				globalState.put(name,
						session.insert(t.getFactObject(name, value)));
				break;
			}
		}
	}

	@Override
	public void deleteGlobal(String name) {
		for (StateTranslator t : globalTranslators) {
			if (t.canTranslate(name)) {
				FactHandle handle = getCachedHandle(name);
				if (handle == null)
					handle = session.getFactHandle(t.getFactObject(name, null));
				session.retract(handle);
				break;
			}
		}
	}

	@Override
	public Serializable get(String name, UUID participantID) {
		for (AgentStateTranslator t : agentTranslators) {
			if (t.canTranslate(name)) {
				FactHandle handle = getCachedHandle(name, participantID);
				if (handle == null)
					handle = session.getFactHandle(t.getFactObject(name,
							participantID, null));
				if (handle != null) {
					Object fact = session.getObject(handle);
					return t.getStateFromFact(fact);
				}
			}
		}
		return null;
	}

	@Override
	public void change(String name, UUID participantID, StateTransformer change) {
		stateChange.add(new StateChange(name, participantID, change));
	}

	@Override
	public void change(String name, UUID participantID, Serializable value) {
		stateChange.add(new StateChange(name, participantID, value));
	}

	@Override
	public void create(ParticipantSharedState state) {
		create(state.getName(), state.getParticipantID(), state.getValue());
	}

	@Override
	public void create(String name, UUID participantID, Serializable value) {
		for (AgentStateTranslator t : agentTranslators) {
			if (t.canTranslate(name)) {
				agentState.get(participantID).put(
						name,
						session.insert(t.getFactObject(name, participantID,
								value)));
				break;
			}
		}
	}

	@Override
	public void delete(String name, UUID participantID) {
		for (AgentStateTranslator t : agentTranslators) {
			if (t.canTranslate(name)) {
				FactHandle handle = getCachedHandle(name, participantID);
				if (handle == null)
					handle = session.getFactHandle(t.getFactObject(name,
							participantID, null));
				session.retract(handle);
				break;
			}
		}
	}

	@Override
	public void incrementTime() {
		clock.advanceTime(1, TimeUnit.SECONDS);
		updateState();
	}

	@EventListener
	public void initialise(Events.Initialised e) {
		updateState();
	}

	protected void updateState() {
		logger.info("Updating state.");
		while (this.stateChange.peek() != null) {
			StateChange c = stateChange.poll();
			if (c.getParticipantId() == null) {
				for (StateTranslator t : globalTranslators) {
					if (t.canTranslate(c.getKey())) {
						FactHandle handle = getCachedHandle(c.getKey());
						if (handle == null)
							handle = session.getFactHandle(t.getFactObject(
									c.getKey(), null));
						Serializable newValue = c.getChange(this);
						if (handle != null)
							session.update(handle,
									t.getFactObject(c.getKey(), newValue));
						else
							createGlobal(c.getKey(), newValue);
						break;
					}
				}
			} else {
				for (AgentStateTranslator t : agentTranslators) {
					if (t.canTranslate(c.getKey())) {
						FactHandle handle = getCachedHandle(c.getKey(),
								c.getParticipantId());
						if (handle == null)
							handle = session.getFactHandle(t.getFactObject(
									c.getKey(), c.getParticipantId(), null));
						Serializable newValue = c.getChange(this);
						if (handle != null)
							session.update(
									handle,
									t.getFactObject(c.getKey(),
											c.getParticipantId(), newValue));
						else
							createGlobal(c.getKey(), newValue);
						break;
					}
				}
			}
		}
		session.fireAllRules();
	}

	@Override
	public StatefulKnowledgeSession get() {
		return session;
	}

}
