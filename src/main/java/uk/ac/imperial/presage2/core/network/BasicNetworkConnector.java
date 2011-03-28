/**
 * 
 */
package uk.ac.imperial.presage2.core.network;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

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

	final private Logger logger = Logger.getLogger(BasicNetworkConnector.class);
	
	protected List<Message> receivedMessages;
	
	@Inject
	protected BasicNetworkConnector(NetworkController controller,
			NetworkAddressFactory networkAddressFactory, @Assisted UUID id) {
		super(controller, networkAddressFactory, id);
		receivedMessages = new LinkedList<Message>();
	}

	/**
	 * <p>Returns the list of messages we have. </p>
	 * <p>After returning these message we will clear our list, therefore
	 * the caller must store these messages if they want to use them later</p>
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
		if(logger.isDebugEnabled())
			logger.debug("Received message: "+ m.toString());
		this.receivedMessages.add(m);
	}

	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkConnector#sendMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public void sendMessage(Message m) {
		if(logger.isDebugEnabled())
			logger.debug("Sending message: "+ m.toString());
		this.controller.deliverMessage(m);
	}

}
