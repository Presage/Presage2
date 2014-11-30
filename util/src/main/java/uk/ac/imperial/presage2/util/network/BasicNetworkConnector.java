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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * <p>
 * Basic implementation of a network connector.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class BasicNetworkConnector extends NetworkConnector {

	List<Message> messages = new LinkedList<Message>();

	public BasicNetworkConnector(EnvironmentSharedStateAccess sharedState,
			Participant p) {
		super(sharedState, p);
	}

	/**
	 * <p>
	 * Returns the list of messages we have.
	 * </p>
	 * <p>
	 * After returning these message we will clear our list, therefore the
	 * caller must store these messages if they want to use them later
	 * </p>
	 * 
	 * @see uk.ac.imperial.presage2.util.network.NetworkAdaptor#getMessages()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Message> getMessages() {
		LinkedList<Message> inbox = (LinkedList<Message>) sharedState.get(
				"network.inbox", this.address.getId());
		messages.addAll(inbox);
		inbox.clear();// empty queue
		return messages;
	}

	@Override
	public Set<NetworkAddress> getConnectedNodes()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName()
				+ " does not support getConnectedNodes()");
	}

}
