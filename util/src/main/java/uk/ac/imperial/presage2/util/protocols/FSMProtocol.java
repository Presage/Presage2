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

import java.util.Set;

import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAdaptor;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.util.fsm.FSMDescription;
import uk.ac.imperial.presage2.util.fsm.FSMException;

public class FSMProtocol extends Protocol {

	protected final FSMDescription description;
	protected final NetworkAdaptor network;

	public FSMProtocol(String name, FSMDescription description, NetworkAdaptor network) {
		super(name);
		this.description = description;
		this.network = network;
	}

	@Override
	public Conversation spawn() {
		try {
			return spawnAsInititor(new ConversationSpawnEvent());
		} catch (FSMException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Conversation spawn(NetworkAddress with) {
		try {
			return spawnAsInititor(new ConversationSpawnEvent(with));
		} catch (FSMException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Conversation spawn(Set<NetworkAddress> with) {
		try {
			return spawnAsInititor(new ConversationSpawnEvent(with));
		} catch (FSMException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void spawn(Message<?> m) {
		FSMConversation conv = new FSMConversation(description, this.name, Role.REPLIER, network);
		try {
			conv.fsm.applyEvent(m);
			activeConversations.add(conv);
		} catch (FSMException e) {
			// TODO log this
		}
	}

	protected Conversation spawnAsInititor(Object event) throws FSMException {
		FSMConversation conv = new FSMConversation(description, this.name, Role.INITIATOR, network);
		conv.fsm.applyEvent(event);
		this.activeConversations.add(conv);
		return conv;
	}

}
