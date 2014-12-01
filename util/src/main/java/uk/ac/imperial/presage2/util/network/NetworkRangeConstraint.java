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

import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.environment.CommunicationRangeService;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.LocationService;

import com.google.inject.Inject;

@ServiceDependencies({ LocationService.class, CommunicationRangeService.class })
public class NetworkRangeConstraint implements NetworkConstraint {

	private LocationService locService;
	private CommunicationRangeService commRangeService;

	@Inject
	NetworkRangeConstraint(EnvironmentServiceProvider serviceProvider)
			throws UnavailableServiceException {
		super();
		locService = serviceProvider
				.getEnvironmentService(LocationService.class);
		commRangeService = serviceProvider
				.getEnvironmentService(CommunicationRangeService.class);
	}

	@Override
	public Message constrainMessage(Message m) {
		// we don't need to modify messages, we just block at point of delivery.
		return m;
	}

	@Override
	public boolean blockMessageDelivery(NetworkAddress from, NetworkAddress to) {
		final UUID a1 = from.getId();
		final UUID a2 = to.getId();
		final Location senderLoc = locService.getAgentLocation(a1);
		final Location receiverLoc = locService.getAgentLocation(a2);
		final double senderRange = commRangeService
				.getAgentCommunicationRange(a1);
		final double receiverRange = commRangeService
				.getAgentCommunicationRange(a2);
		// return true if distance between sender and receiver > the
		// smallest of their comm ranges.
		boolean result = (senderLoc.distanceTo(receiverLoc) > Math.min(
				senderRange, receiverRange));
		return result;
	}

}
