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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.simulator.Scenario;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * <p>
 * A {@link NetworkConnector} which implements {@link #getConnectedNodes()}.
 * </p>
 * 
 * <p>
 * Requires the {@link NetworkController} to understand {@link Ping} messages
 * and respond with a {@link Pong}. This {@link Pong} tells us the
 * {@link NetworkAddress}s of connected agents so we can update our knowledge to
 * reflect this.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class NetworkConnectorWithNodeDiscovery extends BasicNetworkConnector
		implements TimeDriven {

	Time time;
	Set<NetworkAddress> knownNodes = new HashSet<NetworkAddress>();
	Set<NetworkAddress> links = new HashSet<NetworkAddress>();

	@Inject
	protected NetworkConnectorWithNodeDiscovery(NetworkChannel controller,
			NetworkAddressFactory networkAddressFactory, @Assisted UUID id) {
		super(controller, networkAddressFactory, id);
	}

	public NetworkConnectorWithNodeDiscovery(NetworkChannel controller,
			NetworkAddress address, Time t, Scenario s) {
		super(controller, address);
		this.time = t;
		this.registerTimeDriven(s);
	}

	@Inject
	void setTime(Time t) {
		this.time = t;
	}

	@Inject
	public void registerTimeDriven(Scenario s) {
		s.addTimeDriven(this);
	}

	@Override
	public Set<NetworkAddress> getConnectedNodes()
			throws UnsupportedOperationException {
		// return our current perception of connected agents.
		links.remove(address);
		return links;
	}

	@Override
	public synchronized void deliverMessage(Message m) {
		// hijack pongs
		if (m instanceof Pong) {
			Pong p = (Pong) m;
			links = p.getLinks();
			knownNodes.addAll(links);
		} else
			super.deliverMessage(m);
	}

	@Override
	public void incrementTime() {
		this.controller.deliverMessage(new Ping(this.address, this.time));
		this.time.increment();
	}

}
