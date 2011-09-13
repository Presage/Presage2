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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationResponse;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.core.network.NetworkAdaptor;
import uk.ac.imperial.presage2.core.network.NetworkConnectorFactory;
import uk.ac.imperial.presage2.core.participant.Participant;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

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
	protected EnvironmentConnector environment;

	/**
	 * Connector to the network.
	 */
	protected NetworkAdaptor network;

	/**
	 * The agent's perception of time.
	 */
	private Time time;

	/**
	 * FIFO queue of inputs to be processed.
	 */
	protected Queue<Input> inputQueue;

	/**
	 * Set of {@link EnvironmentService}s available to the agent.
	 */
	protected final Set<EnvironmentService> services = new HashSet<EnvironmentService>();

	/**
	 * Persistence of this agent into the database.
	 */
	protected PersistentAgent persist = null;

	/**
	 * Assisted Inject constructor.
	 * 
	 * @param id
	 * @param name
	 * @param environment
	 * @param network
	 * @param time
	 */
	protected AbstractParticipant(@Assisted UUID id, @Assisted String name,
			EnvironmentConnector environment, NetworkAdaptor network, Time time) {
		super();
		this.id = id;
		this.name = name;
		this.environment = environment;
		this.network = network;
		this.time = time;
		this.logger = Logger.getLogger(this.getName() + "(" + this.getID()
				+ ")");
		if (logger.isDebugEnabled()) {
			logger.debug("Created Participant " + this.getName() + ", UUID: "
					+ this.getID());
		}
	}

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
	public Time getTime() {
		return this.time;
	}

	@Override
	public void incrementTime() {
		this.execute();
		this.getTime().increment();
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Inject(optional = true)
	public void initialiseEnvironment(EnvironmentConnector e) {
		this.environment = e;
	}

	@Inject(optional = true)
	public void initialiseNetwork(NetworkConnectorFactory networkFactory) {
		this.network = networkFactory.create(this.getID());
	}

	@Inject(optional = true)
	public void initialiseTime(Time t) {
		this.time = t;
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
	@Override
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
			this.persist.setRegisteredAt(this.getTime().intValue());
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
		}
	}

	/**
	 * Get the set of shared states that this Participant has. Used for the
	 * environment registration request for this participant.
	 * 
	 * @return
	 */
	protected Set<ParticipantSharedState<?>> getSharedState() {
		return new HashSet<ParticipantSharedState<?>>();
	}

	/**
	 * Initialises {@link AbstractParticipant#inputQueue} which will be a FIFO
	 * queue for Inputs received by the Participant.
	 */
	protected void initialiseInputQueue() {
		// Linked list provides a FIFO queue implementation
		this.inputQueue = new LinkedList<Input>();
	}

	@Override
	public void enqueueInput(Input input) {
		this.inputQueue.add(input);
	}

	@Override
	public void enqueueInput(Collection<? extends Input> inputs) {
		this.inputQueue.addAll(inputs);
	}

	/**
	 * <p>
	 * This provides an example start to an agent's
	 * {@link TimeDriven#incrementTime()} method. You may want to use this by
	 * using <code>this.execute()</code> at the top of your implementation, or
	 * not use it at all.
	 * </p>
	 * <p>
	 * In this implementation we simply pull in any messages sent from the
	 * network, then process our entire message queue. Obviously, in order for
	 * the agent to be anything more than purely reactive you should add more to
	 * this.
	 * </p>
	 */
	@Override
	public void execute() {

		// pull in Messages from the network
		enqueueInput(this.network.getMessages());

		// process inputs
		while (this.inputQueue.size() > 0) {
			this.processInput(this.inputQueue.poll());
		}

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

	/**
	 * Process the given input.
	 * 
	 * @param in
	 */
	abstract protected void processInput(Input in);

	@Override
	public void onSimulationComplete() {
		// empty - override is optional if the participant implementor wants to
		// use this method.
	}

}
