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
package uk.ac.imperial.presage2.util.protocols;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAdaptor;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.fsm.FSM;
import uk.ac.imperial.presage2.util.fsm.FSMDescription;
import uk.ac.imperial.presage2.util.fsm.FSMException;

public class FSMConversation implements Conversation {

	final FSM fsm;
	private final String protocol;
	private UUID id;
	private final Role role;
	private final NetworkAdaptor networkWrapper;
	public final List<NetworkAddress> recipients;
	public Object entity;

	FSMConversation(final FSMDescription desc, String protocol, final Role role,
			final NetworkAdaptor network) {
		this.id = Random.randomUUID();
		this.protocol = protocol;
		this.role = role;
		this.recipients = new ArrayList<NetworkAddress>();
		this.networkWrapper = new NetworkAdaptorWrapper(network);
		this.entity = null;
		fsm = new FSM(desc, this);
	}

	@Override
	public boolean canHandle(final Input in) {
		if (in instanceof Message) {
			Message<?> m = (Message<?>) in;
			return m.getConversationKey().equals(id);
		}
		return false;
	}

	@Override
	public void handle(Input in) {
		try {
			fsm.applyEvent(in);
		} catch (FSMException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public UUID getID() {
		return this.id;
	}

	@Override
	public String getState() {
		return fsm.getState();
	}

	@Override
	public boolean isFinished() {
		return fsm.isEndState();
	}

	@Override
	public Role getRole() {
		return this.role;
	}

	public NetworkAdaptor getNetwork() {
		return this.networkWrapper;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setConversationKey(UUID id) {
		this.id = id;
	}

	/**
	 * Wrapper for the {@link NetworkAdaptor} to force the conversationkey to be
	 * set for any outgoing message from this entity.
	 * 
	 * @author Sam Macbeth
	 * 
	 */
	private class NetworkAdaptorWrapper implements NetworkAdaptor {
		private final NetworkAdaptor network;

		NetworkAdaptorWrapper(NetworkAdaptor real) {
			this.network = real;
		}

		public List<Message<?>> getMessages() {
			return network.getMessages();
		}

		public void sendMessage(Message<?> m) {
			m.setConversationKey(id);
			m.setProtocol(protocol);
			network.sendMessage(m);
		}

		public NetworkAddress getAddress() {
			return network.getAddress();
		}

		public Set<NetworkAddress> getConnectedNodes() throws UnsupportedOperationException {
			return network.getConnectedNodes();
		}

	}

	@Override
	public Set<NetworkAddress> getMembers() {
		return new HashSet<NetworkAddress>(this.recipients);
	}

}
