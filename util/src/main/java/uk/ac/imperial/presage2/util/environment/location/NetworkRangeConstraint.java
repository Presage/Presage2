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
package uk.ac.imperial.presage2.util.environment.location;

import java.util.UUID;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.NetworkConstraint;

/**
 * @author Sam Macbeth
 *
 */
public class NetworkRangeConstraint implements NetworkConstraint {

	private LocationService locService;
		
	private CommunicationRangeService commRangeService;
	
	@Inject
	public NetworkRangeConstraint(EnvironmentServiceProvider serviceProvider) throws UnavailableServiceException {
		locService = serviceProvider.getEnvironmentService(LocationService.class);
		commRangeService = serviceProvider.getEnvironmentService(CommunicationRangeService.class);
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
		try {
			// retrieve locations and comms ranges of sender and receiver.
			final Location senderLoc 	= locService.getAgentLocation(sender);
			final Location receiverLoc 	= locService.getAgentLocation(receiver);
			final double senderRange 	= commRangeService.getAgentCommunicationRange(sender);
			final double receiverRange 	= commRangeService.getAgentCommunicationRange(receiver);
			
			// return true if distance between sender and receiver > the smallest of their comm ranges.
			return (senderLoc.distanceTo(receiverLoc) > Math.min(senderRange, receiverRange));
		} catch (CannotSeeAgent e) {
			// this should not happen!
			throw new RuntimeException("LocationService threw CannotSeeAgent for NetworkRangeConstraint", e);
		}
	}

}
