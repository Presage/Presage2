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
package uk.ac.imperial.presage2.util.network;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.NetworkAddressFactory;
import uk.ac.imperial.presage2.core.network.NetworkChannel;
import uk.ac.imperial.presage2.core.network.NetworkConnector;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * {@link NetworkConnector} which doesn't do anything.
 * 
 * @author Sam Macbeth
 * 
 */
class DisconnectedNetworkConnector extends NetworkConnector {

	@Inject
	protected DisconnectedNetworkConnector(NetworkChannel controller,
			NetworkAddressFactory networkAddressFactory, @Assisted UUID id) {
		super(controller, networkAddressFactory, id);
	}

	@Override
	public List<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public Set<NetworkAddress> getConnectedNodes()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deliverMessage(Message m) {
	}

	@Override
	public void sendMessage(Message m) {
	}

}