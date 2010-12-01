/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * <p>The NetworkConnector passes messages between a participant and the
 * Network controller</p>
 * 
 * @author Sam Macbeth
 *
 */
public abstract class NetworkConnector implements NetworkAdaptor,
		NetworkChannel {

	final protected Logger logger;
	
	final protected NetworkChannel controller;
	
	final protected UUID parentID;
	
	final protected NetworkAddress address;
	
	/**
	 * <p>Create a NetworkConnector</p>
	 * <p>This constructor uses Guice assisted inject to pass the participant's UUID
	 * as well injecting other required parameters. Do not override this unless you know
	 * what you are doing!</p>
	 * @param controller
	 * @param logger
	 * @param networkAddressFactory factory for creating this connector's network address.
	 * @param id
	 */
	@Inject
	protected NetworkConnector(NetworkChannel controller, 
			Logger logger, 
			NetworkAddressFactory networkAddressFactory, 
			@Assisted UUID id) {
		super();
		this.controller = controller;
		this.logger = logger;
		this.parentID = id;
		this.address = networkAddressFactory.create(parentID);
	}

	/**
	 * <p>Message delivery from NetworkController.</p>
	 * @see org.imperial.isn.presage2.core.network.NetworkChannel#deliverMessage(org.imperial.isn.presage2.core.network.Message)
	 */
	@Override
	public abstract void deliverMessage(Message m) throws NetworkException;

	/**
	 * Participant requesting to send a message.
	 * @see org.imperial.isn.presage2.core.network.NetworkAdaptor#sendMessage(org.imperial.isn.presage2.core.network.Message)
	 */
	@Override
	public abstract void sendMessage(Message m) throws NetworkException;

}
