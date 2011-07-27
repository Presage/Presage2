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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.simulator.Scenario;

import com.google.inject.Inject;

/**
 * <p>
 * A {@link NetworkController} which allows the use of {@link NetworkConstraint}
 * s.
 * </p>
 * 
 * <p>
 * {@link NetworkConstraint}s may modify messages when they are received from
 * the sender, or block the sending of a message at the point of delivery to an
 * individual.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class ConstrainedNetworkController extends NetworkController {

	private final Logger logger = Logger
			.getLogger(ConstrainedNetworkController.class);

	protected Set<NetworkConstraint> constraints;

	private Map<NetworkAddress, Set<NetworkAddress>> blockedCache = new HashMap<NetworkAddress, Set<NetworkAddress>>();

	/**
	 * @param time
	 * @param environment
	 */
	@Inject
	public ConstrainedNetworkController(Time time,
			EnvironmentSharedStateAccess environment, Scenario s) {
		super(time, environment, s);
		constraints = new HashSet<NetworkConstraint>();
	}

	public void addConstraint(NetworkConstraint c) {
		constraints.add(c);
	}

	@Inject(optional = true)
	public void addConstaints(Set<NetworkConstraint> cons) {
		constraints.addAll(cons);
	}

	@Override
	protected void handleMessage(Message m) {
		// apply NetworkConstraints.
		for (NetworkConstraint c : this.constraints) {
			m = c.constrainMessage(m);
		}
		super.handleMessage(m);
	}

	@Override
	protected void deliverMessageTo(NetworkAddress to, Message m) {
		boolean blockMessage = false;
		// ask all networkconstraints if they want to block
		for (NetworkConstraint c : this.constraints) {
			blockMessage = blockMessage || c.blockMessageDelivery(to, m);
		}
		if (blockMessage) {
			if (logger.isDebugEnabled()) {
				logger.debug("Delivery of message " + m + " to " + to
						+ " was blocked by a constraint.");
			}
			if (this.eventBus != null) {
				this.eventBus.publish(new MessageBlockedEvent(time, m, to));
			}
		} else {
			super.deliverMessageTo(to, m);
		}
	}

	@Override
	protected Pong getPong(Ping p) {
		// start with all NetworkAddresses, then determine and remove those
		// which will be blocked by constraints.
		Set<NetworkAddress> links = new HashSet<NetworkAddress>();
		// Set<NetworkAddress> blocked = new HashSet<NetworkAddress>();
		for (NetworkAddress a : this.devices.keySet()) {
			for (NetworkConstraint c : this.constraints) {
				if (!c.blockMessageDelivery(a, p)) {
					links.add(a);
				}
			}
		}
		// links.removeAll(blocked);
		return new Pong(time.clone(), links);
	}

	@Override
	public void incrementTime() {
		super.incrementTime();
		blockedCache.clear();
	}

}
