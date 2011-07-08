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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * <p>
 * Basic implementation of a network connector.
 * </p>
 * 
 * <p>
 * We simply send messages directly over the networkchannel, and store received
 * messages in a list for the participant to retrieve.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class BasicNetworkConnector extends NetworkConnector {

	final private Logger logger = Logger.getLogger(BasicNetworkConnector.class);

	protected List<Message> receivedMessages;

	@Inject
	protected BasicNetworkConnector(NetworkChannel controller,
			NetworkAddressFactory networkAddressFactory, @Assisted UUID id) {
		super(controller, networkAddressFactory, id);
		receivedMessages = new LinkedList<Message>();
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
	 * @see uk.ac.imperial.presage2.core.network.NetworkAdaptor#getMessages()
	 */
	@Override
	public List<Message> getMessages() {
		List<Message> messages = receivedMessages;
		receivedMessages = new LinkedList<Message>();
		return messages;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkConnector#deliverMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	synchronized public void deliverMessage(Message m) {
		if (logger.isDebugEnabled())
			logger.debug("Received message: " + m.toString());
		this.receivedMessages.add(m);
	}

	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkConnector#sendMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public void sendMessage(Message m) {
		if (logger.isDebugEnabled())
			logger.debug("Sending message: " + m.toString());
		this.controller.deliverMessage(m);
	}

	@Override
	public Set<NetworkAddress> getConnectedNodes()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName()
				+ " does not support getConnectedNodes()");
	}

}
