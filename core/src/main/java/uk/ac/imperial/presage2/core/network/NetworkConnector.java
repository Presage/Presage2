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

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * <p>
 * The NetworkConnector passes messages between a participant and the Network
 * controller
 * </p>
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
	 * <p>
	 * Create a NetworkConnector
	 * </p>
	 * <p>
	 * This constructor uses Guice assisted inject to pass the participant's
	 * UUID as well injecting other required parameters. Do not override this
	 * unless you know what you are doing!
	 * </p>
	 * 
	 * @param controller
	 * @param networkAddressFactory
	 *            factory for creating this connector's network address.
	 * @param id
	 */
	@Inject
	protected NetworkConnector(NetworkChannel controller,
			NetworkAddressFactory networkAddressFactory, @Assisted UUID id) {
		super();
		this.controller = controller;
		this.parentID = id;
		this.address = networkAddressFactory.create(parentID);
		// check if we need to register.
		if (controller instanceof RequiresRegistration) {
			((RequiresRegistration) controller)
					.register(new NetworkRegistrationRequest(address, this));
		}

	}

	public NetworkConnector(final NetworkChannel controller,
			final NetworkAddress address) {
		super();
		this.controller = controller;
		this.parentID = address.getId();
		this.address = address;
		if (controller instanceof RequiresRegistration) {
			((RequiresRegistration) controller)
					.register(new NetworkRegistrationRequest(address, this));
		}
	}

	/**
	 * <p>
	 * Message delivery from NetworkController.
	 * </p>
	 * 
	 * @see uk.ac.imperial.presage2.core.network.NetworkChannel#deliverMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public abstract void deliverMessage(Message<?> m);

	/**
	 * Participant requesting to send a message.
	 * 
	 * @see uk.ac.imperial.presage2.core.network.NetworkAdaptor#sendMessage(uk.ac.imperial.presage2.core.network.Message)
	 */
	@Override
	public abstract void sendMessage(Message<?> m);

	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkAdaptor#getAddress()
	 */
	@Override
	public NetworkAddress getAddress() {
		return this.address;
	}

}
