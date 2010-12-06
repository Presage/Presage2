/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * <p>Basic implementation of a network connector.</p>
 * 
 * <p>We simply send messages directly over the networkchannel,
 * and store received messages in a list for the participant to
 * retrieve.</p>
 * 
 * @author Sam Macbeth
 *
 */
public class BasicNetworkConnector extends NetworkConnector {

	protected List<Message> receivedMessages;
	
	@Inject
	protected BasicNetworkConnector(NetworkChannel controller, Logger logger,
			NetworkAddressFactory networkAddressFactory, UUID id) {
		super(controller, logger, networkAddressFactory, id);
		receivedMessages = new LinkedList<Message>();
	}

	/**
	 * <p>Returns the list of messages we have. </p>
	 * <p>After returning these message we will clear our list, therefore
	 * the caller must store these messages if they want to use them later</p>
	 * @see org.imperial.isn.presage2.core.network.NetworkAdaptor#getMessages()
	 */
	@Override
	public List<Message> getMessages() {
		List<Message> messages = receivedMessages;
		receivedMessages = new LinkedList<Message>();
		return messages;
	}

	/**
	 * @see org.imperial.isn.presage2.core.network.NetworkConnector#deliverMessage(org.imperial.isn.presage2.core.network.Message)
	 */
	@Override
	public void deliverMessage(Message m) {
		this.receivedMessages.add(m);
	}

	/**
	 * @see org.imperial.isn.presage2.core.network.NetworkConnector#sendMessage(org.imperial.isn.presage2.core.network.Message)
	 */
	@Override
	public void sendMessage(Message m) {
		this.controller.deliverMessage(m);
	}

}
