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
package uk.ac.imperial.presage2.util.network;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.StateTransformer;
import uk.ac.imperial.presage2.core.simulator.PreStep;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MessageHandler extends EnvironmentService implements ActionHandler {

	Set<NetworkAddress> devices = new HashSet<NetworkAddress>();

	protected Set<NetworkConstraint> constraints = new HashSet<NetworkConstraint>();
	private Map<Pair<NetworkAddress, NetworkAddress>, Boolean> connectionCache = new HashMap<Pair<NetworkAddress, NetworkAddress>, Boolean>();

	@Inject
	MessageHandler(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
	}

	@Inject(optional = true)
	void addConstaints(Set<NetworkConstraint> cons) {
		constraints.addAll(cons);
	}

	@Override
	public boolean canHandle(Action action) {
		return action instanceof Message;
	}

	@Override
	public Object handle(Action action, UUID actor)
			throws ActionHandlingException {
		Message m = (Message) action;
		if (m instanceof UnicastMessage) {
			doUnicast((UnicastMessage) m);
		} else if (m instanceof MulticastMessage) {
			doMulticast((MulticastMessage) m);
		} else if (m instanceof BroadcastMessage) {
			doBroadcast((BroadcastMessage) m);
		} else {
			throw new UnknownMessageTypeException(m);
		}
		return null;
	}

	private void doUnicast(UnicastMessage m) {
		this.deliverMessageTo(m.getTo(), m);
	}

	private void doMulticast(MulticastMessage m) {
		final List<NetworkAddress> recipients = m.getTo();
		for (NetworkAddress to : recipients) {
			this.deliverMessageTo(to, m);
		}
	}

	private void doBroadcast(BroadcastMessage m) {
		for (NetworkAddress to : this.devices) {
			// deliver to all but sender
			if (m.getFrom() != to)
				this.deliverMessageTo(to, m);
		}
	}

	private void deliverMessageTo(NetworkAddress to, Message m) {
		this.sharedState.getGlobal("network.devices");
		if (devices.contains(to) && canDeliver(m.getFrom(), to)) {
			this.sharedState.change("network.inbox", to.getId(),
					new QueueMessage(constrainMessage(m)));
		}
	}

	/**
	 * Updates handler cache for new timestep. Called as a {@link PreStep}
	 * method by the scheduler.
	 */
	@SuppressWarnings("unchecked")
	@PreStep
	public void updateDevices() {
		Serializable d = this.sharedState.getGlobal("network.devices");
		if (d != null)
			this.devices = (Set<NetworkAddress>) d;
		connectionCache.clear();
	}

	/**
	 * Check whether a message may be sent to a network address, or if a
	 * constraint is blocking it. Results are cached for one time-step.
	 * 
	 * @param to
	 * @param m
	 * @return
	 */
	private boolean canDeliver(NetworkAddress from, NetworkAddress to) {
		final Pair<NetworkAddress, NetworkAddress> link = Pair.of(from, to);
		if (connectionCache.containsKey(link)) {
			return connectionCache.get(link);
		} else {
			boolean block = false;
			// ask all networkconstraints if they want to block
			for (NetworkConstraint c : this.constraints) {
				block = block || c.blockMessageDelivery(from, to);
			}
			connectionCache.put(link, !block);
			return !block;
		}
	}

	/**
	 * Modify a message before sending, as specified by network constraints.
	 * 
	 * @param m
	 * @return
	 */
	private Message constrainMessage(Message m) {
		for (NetworkConstraint c : this.constraints) {
			m = c.constrainMessage(m);
		}
		return m;
	}

	static class QueueMessage implements StateTransformer {
		final Message m;

		QueueMessage(Message m) {
			super();
			this.m = m;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Serializable transform(Serializable state) {
			LinkedList<Message> queue;
			if (state == null)
				queue = new LinkedList<Message>();
			else
				queue = (LinkedList<Message>) state;
			queue.add(m);
			return queue;
		}

	}

	public Set<NetworkAddress> getConnectedNodes(NetworkAddress from) {
		if (constraints.size() == 0)
			return Collections.unmodifiableSet(devices);
		else {
			Set<NetworkAddress> connected = new HashSet<NetworkAddress>();
			for (NetworkAddress n : devices) {
				if (canDeliver(from, n)) {
					connected.add(n);
				}
			}
			return connected;
		}
	}

}
