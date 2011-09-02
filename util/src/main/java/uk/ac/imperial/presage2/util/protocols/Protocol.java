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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.core.messaging.InputHandler;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;

public abstract class Protocol implements InputHandler {

	protected final String name;

	protected List<Conversation> activeConversations = new LinkedList<Conversation>();

	protected Protocol(String name) {
		this.name = name;
	}

	@Override
	public boolean canHandle(Input in) {
		if (in instanceof Message) {
			return ((Message<?>) in).getProtocol().equals(name);
		}
		return false;
	}

	@Override
	public void handle(Input in) {
		// pass this input to all conversation that could handle it.
		int handleCount = 0;
		for (Iterator<Conversation> it = activeConversations.iterator(); it.hasNext();) {
			Conversation c = it.next();
			if (c.canHandle(in)) {
				c.handle(in);
				handleCount++;
			}
			if (c.isFinished())
				it.remove();
		}
		if (handleCount == 0) {
			spawn((Message<?>) in);
		}
	}

	/**
	 * Spawn a conversation of this protocol. No arguments are provided so this
	 * will usually be a broadcast conversation.
	 */
	public abstract Conversation spawn();

	/**
	 * Spawn a conversation of this protocol with the given network address.
	 * 
	 * @param with
	 */
	public abstract Conversation spawn(NetworkAddress with);

	/**
	 * Spawn a conversation of this protocol with the given network addresses
	 * (multi-cast conversation).
	 * 
	 * @param with
	 * @return
	 */
	public abstract Conversation spawn(Set<NetworkAddress> with);

	/**
	 * Spawn a conversation of this protocol from the given input message
	 * (reactive conversation).
	 * 
	 * @param m
	 * @return
	 */
	public abstract void spawn(Message<?> m);

	/**
	 * Get the set of active conversations managed by this protocol.
	 * 
	 * @return
	 */
	public Set<Conversation> getActiveConversations() {
		return null;
	}

}
