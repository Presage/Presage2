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

package uk.ac.imperial.presage2.core.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.simulator.Scenario;

/**
 * <p>This is a central controller through which all messages go.</p>
 * 
 * <p>Like the environment it ensures agents obey the rules of the system. In
 * this case these are to do with the network, such as transmission ranges etc.</p>
 * 
 * @author Sam Macbeth
 *
 */
@Singleton
public class NetworkController implements NetworkChannel, TimeDriven, RequiresRegistration {

	private final Logger logger = Logger.getLogger(NetworkController.class);
	
	protected Time time;
	
	protected List<Message> toDeliver;
	
	/**
	 * Map of devices registered to this controller.
	 */
	protected Map<NetworkAddress, NetworkChannel> devices;
	
	/**
	 * Access to environment shared state.
	 */
	protected EnvironmentSharedStateAccess environment;
	
	/**
	 * @param time
	 */
	@Inject
	public NetworkController(Time time, EnvironmentSharedStateAccess environment, Scenario s) {
		super();
		this.time = time;
		this.environment = environment;
		this.devices = new HashMap<NetworkAddress, NetworkChannel>();
		this.toDeliver = new LinkedList<Message>();
		s.addTimeDriven(this);
	}
	
	/**
	 * @see uk.ac.imperial.presage2.core.TimeDriven#incrementTime()
	 */
	@Override
	public void incrementTime() {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Delivering messages for time "+this.time.toString());
		}
		for(Message m : this.toDeliver) {
			try {
				this.handleMessage(m);
			} catch(NetworkException e) {
				// log exceptions we encounter (unchecked runtime exceptions)
				this.logger.warn(e.getMessage(), e);
			}
		}
		this.toDeliver = new LinkedList<Message>();
		this.time.increment();
	}

	/**
	 * <p>Invoked by a NetworkConnector when it wishes to send a message.</p>
	 * <p>In this implementation we deliver at the end of time cycle, therefore this function just adds
	 * the message to the delivery queue</p>
	 * @see uk.ac.imperial.presage2.core.network.NetworkChannel#deliverMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	synchronized public void deliverMessage(Message m) {
		this.toDeliver.add(m);
	}
	
	protected void handleMessage(Message m) {
		// check message type
		if(m instanceof UnicastMessage) {
			doUnicast((UnicastMessage) m);
		} else if(m instanceof MulticastMessage) {
			doMulticast((MulticastMessage) m);
		} else if(m instanceof BroadcastMessage) {
			doBroadcast((BroadcastMessage) m);
		} else {
			throw new UnknownMessageTypeException(m);
		}
	}

	/**
	 * Send a unicast message
	 * @param m
	 * @throws NetworkException
	 */
	protected void doUnicast(UnicastMessage m) {
		try {
			this.deliverMessageTo(m.getTo(), m);
			if(this.logger.isDebugEnabled()) {
				this.logger.debug("Dispatched unicast message: "+ m.toString());
			}
		} catch(NullPointerException e) {
			throw new UnreachableRecipientException(m, m.getTo(), e);
		}
	}
	
	/**
	 * Send a multicast message
	 * @param m
	 */
	protected void doMulticast(MulticastMessage m) {
		final List<NetworkAddress> recipients = m.getTo();
		final List<NetworkAddress> unreachable = new LinkedList<NetworkAddress>();
		for(NetworkAddress to : recipients) {
			try {		
				this.deliverMessageTo(to, m);
			} catch(NullPointerException e) {
				unreachable.add(to);
			}
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Sent multicast message: "+ m.toString());
		}
		if(unreachable.size() > 0) {
			throw new UnreachableRecipientException(m, unreachable);
		}
	}
	
	/**
	 * Send a broadcast message
	 * @param m
	 */
	protected void doBroadcast(BroadcastMessage m) {
		for(NetworkAddress to : this.devices.keySet()) {
			// deliver to all but sender
			if(m.getFrom() != to)
				this.deliverMessageTo(to, m);
		}
		this.logger.debug("Sent broadcast message: "+ m.toString());
	}

	/**
	 * Register a network device with this NetworkController
	 * @param req
	 * @throws NetworkException If the request is null, or one of the
	 * 		request's parameters is null (Address or link).
	 */
	synchronized public void register(NetworkRegistrationRequest req) {
		// defensive programming
		if(req == null || req.getAddress() == null || req.getLink() == null) {
				throw new NullPointerException("NetworkRegistrationRequest null or containing null parameters");
		}

		this.devices.put(req.getAddress(), req.getLink());
	}

	/**
	 * Deliver a message m to recipient to.
	 * @param to
	 * @param m
	 */
	protected void deliverMessageTo(NetworkAddress to, Message m) {
		this.devices.get(to).deliverMessage(m);
	}

}
