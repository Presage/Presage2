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

package uk.ac.imperial.presage2.util.participant;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationResponse;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.simulator.Initialisor;

import com.google.inject.Inject;

/**
 * <p>
 * This implements the core of a {@link Participant} to manage the majority of
 * the mundane functions allowing the user to start writing the agent's
 * behaviours sooner. It implements {@link EnvironmentServiceProvider} to
 * provide an interface to {@link EnvironmentService}s that are available to the
 * agent
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class AbstractParticipant implements Participant,
		EnvironmentServiceProvider {

	/**
	 * {@link Logger} for this agent.
	 */
	protected final Logger logger;

	/**
	 * This Participant's unique ID.
	 */
	private UUID id;

	/**
	 * A human readable name for this Participant.
	 */
	private String name;

	/**
	 * This Participant's authkey obtained when registering with the
	 * environment.
	 */
	protected UUID authkey;

	/**
	 * Connector to the environment the participant is in.
	 */
	@Inject(optional = true)
	protected EnvironmentConnector environment;

	/**
	 * FIFO queue of inputs to be processed.
	 */
	protected Queue<Object> inputQueue;

	/**
	 * Set of {@link EnvironmentService}s available to the agent.
	 */
	protected final Set<EnvironmentService> services = new HashSet<EnvironmentService>();

	/**
	 * Persistence of this agent into the database.
	 */
	protected PersistentAgent persist = null;

	private Map<String, State<?>> dynamicFields = new HashMap<String, State<?>>();

	/**
	 * <p>
	 * Basic Participant constructor.
	 * </p>
	 * 
	 * <p>
	 * Requires environment & network to be injected by field injection. This
	 * can be done either by creating this object with a guice injector or by
	 * using on-demand injection:
	 * 
	 * <pre class="prettyprint">
	 * Injector injector = Guice.createInjector(...);
	 * 
	 * RealParticipant participant = new RealParticipant(...);
	 * injector.injectMembers(participant);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param id
	 * @param name
	 */
	public AbstractParticipant(UUID id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.logger = Logger.getLogger(this.getName());
		if (logger.isDebugEnabled()) {
			logger.debug("Created Participant " + this.getName() + ", UUID: "
					+ this.getID());
		}
	}

	@Override
	public UUID getID() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Inject(optional = true)
	public void persistParticipant(StorageService storage) {
		this.persist = storage.createAgent(getID(), getName());
		this.persist.setProperty("type", getClass().getSimpleName());
	}

	/**
	 * <p>
	 * The initialisation process for the AbstractParticipant involves the
	 * following:
	 * </p>
	 * <ul>
	 * <li>Registering with the environment.</li>
	 * <li>Creating a Queue for incoming {@link Input}s</li>
	 * </ul>
	 * We split these up into protected function calls in case the implementor
	 * wishes to override only certain parts of this process.
	 */
	@Initialisor(nice = -20)
	public void initialise() {
		registerWithEnvironment();
		initialiseInputQueue();
	}

	/**
	 * <p>
	 * Registers this Participant with the environment.
	 * </p>
	 * <p>
	 * Creates an {@link EnvironmentRegistrationRequest} and uses it to register
	 * with the host environment via
	 * {@link EnvironmentConnector#register(EnvironmentRegistrationRequest)}.
	 * The shared state set for this request is obtained from
	 * {@link AbstractParticipant#getSharedState()}.
	 */
	private void registerWithEnvironment() {
		// Create base registration request
		EnvironmentRegistrationRequest request = new EnvironmentRegistrationRequest(
				getID(), this);
		// Add any shared state we have
		request.setSharedState(this.getSharedState());
		// Register
		EnvironmentRegistrationResponse response = environment
				.register(request);
		// Save the returned authkey
		this.authkey = response.getAuthKey();
		// process the returned environment services
		processEnvironmentServices(response.getServices());

		if (this.persist != null) {
			this.persist.setRegisteredAt(0);
		}
	}

	/**
	 * <p>
	 * Process the {@link EnvironmentService}s from environment registration.
	 * </p>
	 * <p>
	 * This will probably involve looking for ones you can use, pulling them
	 * out, and casting them to the correct type.
	 * </p>
	 * 
	 * @param services
	 */
	protected void processEnvironmentServices(Set<EnvironmentService> services) {
		for (EnvironmentService s : services) {
			this.services.add(s);
			for (Method m : s.getClass().getMethods()) {
				StateAccessor a = m.getAnnotation(StateAccessor.class);
				if (a != null && dynamicFields.containsKey(a.value())) {
					State<?> state = dynamicFields.get(a.value());
					Class<?>[] params = m.getParameterTypes();
					boolean validMethod = params.length == 1
							&& params[0].equals(UUID.class);
					validMethod &= m.getReturnType().isInstance(
							state.initialValue);
					if (!validMethod) {
						logger.warn("@StateAccessor method "
								+ m.getName()
								+ " is not compatable with dynamic state field "
								+ state + ", this field won't update!");
					} else {
						state.setDataSource(Pair.of(m, s));
					}
				}
			}
		}
	}

	/**
	 * Get the set of shared states that this Participant has. Used for the
	 * environment registration request for this participant.
	 * 
	 * @return
	 */
	protected Set<ParticipantSharedState> getSharedState() {
		Set<ParticipantSharedState> ss = new HashSet<ParticipantSharedState>();
		// search for State objects
		for (Field f : this.getClass().getFields()) {
			if (f.getType().equals(State.class)) {
				try {
					State<?> state = (State<?>) f.get(this);
					ss.add(new ParticipantSharedState(state.key,
							(Serializable) state.initialValue, getID()));
					if (dynamicFields.containsKey(state.key)) {
						logger.warn("Duplicate state fields for ke "
								+ state.key);
						f.set(this, dynamicFields.get(state.key));
					} else {
						dynamicFields.put(state.key, state);
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					logger.warn("Cannot access state field " + f.getName(), e);
				}
			}
		}
		return ss;
	}

	/**
	 * Initialises {@link AbstractParticipant#inputQueue} which will be a FIFO
	 * queue for Inputs received by the Participant.
	 */
	protected void initialiseInputQueue() {
		// Linked list provides a FIFO queue implementation
		this.inputQueue = new LinkedList<Object>();
	}

	@Override
	public void enqueueInput(Object input) {
		this.inputQueue.add(input);
	}

	@Override
	public void enqueueInput(Collection<? extends Object> inputs) {
		this.inputQueue.addAll(inputs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends EnvironmentService> T getEnvironmentService(Class<T> type)
			throws UnavailableServiceException {
		for (EnvironmentService s : this.services) {
			if (s.getClass() == type) {
				return (T) s;
			}
		}
		throw new UnavailableServiceException(type);
	}

	public void act(Action a) throws ActionHandlingException {
		if (authkey == null)
			throw new ActionHandlingException(
					"Agent is not registered with environment yet.");
		this.environment.act(a, getID(), authkey);
	}

	<T extends Object> State<T> createState(String key, T initialValue) {
		return this.new State<T>(key, initialValue);
	}

	public class State<T extends Object> {
		final String key;
		final T initialValue;
		Pair<Method, ? extends Object> dataSource = null;

		public State(String key, T initialValue) {
			super();
			this.key = key;
			this.initialValue = initialValue;
		}

		@SuppressWarnings("unchecked")
		public T get() {
			if (dataSource == null) {
				throw new RuntimeException(
						"Uninitialised dynamic field, is there a @StateAccessor for the key "
								+ key + "?");
			}
			try {
				return (T) dataSource.getLeft().invoke(dataSource.getRight(),
						getID());
			} catch (Exception e) {
				logger.warn("Dynamic field error", e);
				throw new RuntimeException(e);
			}
		}

		void setDataSource(Pair<Method, ? extends Object> dataSource) {
			this.dataSource = dataSource;
		}
	}

}
