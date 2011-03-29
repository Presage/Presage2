/**
 * 
 */
package uk.ac.imperial.presage2.core.network;

import java.util.UUID;

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
	
	final protected NetworkChannel controller;
	
	final protected UUID parentID;
	
	final protected NetworkAddress address;
	
	/**
	 * <p>Create a NetworkConnector</p>
	 * <p>This constructor uses Guice assisted inject to pass the participant's UUID
	 * as well injecting other required parameters. Do not override this unless you know
	 * what you are doing!</p>
	 * @param controller
	 * @param networkAddressFactory factory for creating this connector's network address.
	 * @param id
	 */
	@Inject
	protected NetworkConnector(NetworkChannel controller, 
			NetworkAddressFactory networkAddressFactory, 
			@Assisted UUID id) {
		super();
		this.controller = controller;
		this.parentID = id;
		this.address = networkAddressFactory.create(parentID);
		//controller.registerConnector(new NetworkRegistrationRequest(address, this));
	}

	/**
	 * <p>Message delivery from NetworkController.</p>
	 * @see uk.ac.imperial.presage2.core.network.NetworkChannel#deliverMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public abstract void deliverMessage(Message m);

	/**
	 * Participant requesting to send a message.
	 * @see uk.ac.imperial.presage2.core.network.NetworkAdaptor#sendMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public abstract void sendMessage(Message m);

	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkAdaptor#getAddress()
	 */
	@Override
	public NetworkAddress getAddress() {
		return this.address;
	}

}
