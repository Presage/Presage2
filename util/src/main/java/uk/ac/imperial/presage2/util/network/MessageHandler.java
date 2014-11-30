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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.StateTransformer;
import uk.ac.imperial.presage2.core.simulator.PreStep;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MessageHandler implements ActionHandler {

	EnvironmentSharedStateAccess sharedState;

	Set<NetworkAddress> devices = new HashSet<NetworkAddress>();

	@Inject
	MessageHandler(EnvironmentSharedStateAccess sharedState) {
		super();
		this.sharedState = sharedState;
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

	protected void doUnicast(UnicastMessage m) {
		this.deliverMessageTo(m.getTo(), m);
	}

	protected void doMulticast(MulticastMessage m) {
		final List<NetworkAddress> recipients = m.getTo();
		for (NetworkAddress to : recipients) {
			this.deliverMessageTo(to, m);
		}
	}

	protected void doBroadcast(BroadcastMessage m) {
		for (NetworkAddress to : this.devices) {
			// deliver to all but sender
			if (m.getFrom() != to)
				this.deliverMessageTo(to, m);
		}
	}

	protected void deliverMessageTo(NetworkAddress to, Message m) {
		this.sharedState.getGlobal("network.devices");
		if (devices.contains(to)) {
			this.sharedState.change("network.inbox", to.getId(),
					new QueueMessage(m));
		}
	}
	
	@SuppressWarnings("unchecked")
	@PreStep
	public void updateDevices() {
		Serializable d = this.sharedState.getGlobal("network.devices");
		if(d != null)
			this.devices = (Set<NetworkAddress>) d;
		
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

}
