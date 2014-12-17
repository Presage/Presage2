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

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.StateTransformer;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * <p>
 * The NetworkConnector passes messages between a participant and the Network
 * controller
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class NetworkConnector extends EnvironmentService implements
		NetworkAdaptor {

	final protected NetworkAddress address;
	final protected Participant p;

	public NetworkConnector(EnvironmentSharedStateAccess sharedState,
			Participant p) {
		super(sharedState);
		this.p = p;
		this.address = new NetworkAddress(p.getID());
		// create inbox
		sharedState.create("network.inbox", address.getId(),
				new LinkedList<Message>());
		// register address on network
		sharedState.changeGlobal("network.devices", new StateTransformer() {
			@SuppressWarnings("unchecked")
			@Override
			public Serializable transform(Serializable state) {
				HashSet<NetworkAddress> devices;
				if (state == null)
					devices = new HashSet<NetworkAddress>();
				else
					devices = (HashSet<NetworkAddress>) state;
				devices.add(address);
				return devices;
			}
		});
	}

	/**
	 * @see uk.ac.imperial.presage2.util.network.NetworkAdaptor#getAddress()
	 */
	@Override
	public NetworkAddress getAddress() {
		return this.address;
	}

	@Override
	public void sendMessage(Message m) throws ActionHandlingException {
		p.act(m);
	}

}
