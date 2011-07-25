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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.SharedStateAccessException;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.NetworkConstraint;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.util.environment.CommunicationRangeService;
import uk.ac.imperial.presage2.util.location.CannotSeeAgent;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.LocationService;

import com.google.inject.Inject;

/**
 * @author Sam Macbeth
 * 
 */
@ServiceDependencies({ LocationService.class, CommunicationRangeService.class })
public class NetworkRangeConstraint implements NetworkConstraint, TimeDriven {

	private LocationService locService;

	private CommunicationRangeService commRangeService;

	private Map<UUID, Map<UUID, Boolean>> blockCache = new HashMap<UUID, Map<UUID, Boolean>>();

	@Inject
	public NetworkRangeConstraint(EnvironmentServiceProvider serviceProvider,
			Scenario s) throws UnavailableServiceException {
		locService = serviceProvider
				.getEnvironmentService(LocationService.class);
		commRangeService = serviceProvider
				.getEnvironmentService(CommunicationRangeService.class);
		s.addTimeDriven(this);
	}

	@Override
	public Message constrainMessage(Message m) {
		// we don't need to modify messages, we just block at point of delivery.
		return m;
	}

	@Override
	public boolean blockMessageDelivery(NetworkAddress to, Message m) {
		final UUID sender = m.getFrom().getId();
		final UUID receiver = to.getId();
		if (blockCache.containsKey(sender)
				&& blockCache.get(sender).containsKey(receiver)) {
			return blockCache.get(sender).get(receiver);
		}
		try {
			// retrieve locations and comms ranges of sender and receiver.
			final Location senderLoc = locService.getAgentLocation(sender);
			final Location receiverLoc = locService.getAgentLocation(receiver);
			final double senderRange = commRangeService
					.getAgentCommunicationRange(sender);
			final double receiverRange = commRangeService
					.getAgentCommunicationRange(receiver);

			// return true if distance between sender and receiver > the
			// smallest of their comm ranges.
			boolean result = (senderLoc.distanceTo(receiverLoc) > Math.min(
					senderRange, receiverRange));
			addResult(sender, receiver, result);
			return result;
		} catch (CannotSeeAgent e) {
			// this should not happen!
			throw new RuntimeException(
					"LocationService threw CannotSeeAgent for NetworkRangeConstraint",
					e);
		} catch (SharedStateAccessException e) {
			// someone doesn't have location or communication range state, allow
			// in this case
			return false;
		}
	}

	private void addResult(UUID sender, UUID receiver, boolean result) {
		if (!blockCache.containsKey(sender)) {
			synchronized (blockCache) {
				blockCache.put(sender, Collections
						.synchronizedMap(new HashMap<UUID, Boolean>()));
			}
		}
		if (!blockCache.containsKey(receiver)) {
			synchronized (blockCache) {
				blockCache.put(receiver, Collections
						.synchronizedMap(new HashMap<UUID, Boolean>()));
			}
		}
		blockCache.get(sender).put(receiver, result);
		blockCache.get(receiver).put(sender, result);
	}

	@Override
	public void incrementTime() {
		blockCache.clear();
	}

}
