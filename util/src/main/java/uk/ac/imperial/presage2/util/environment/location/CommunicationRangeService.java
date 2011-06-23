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

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;

/**
 * An {@link EnvironmentService} to retrieve agents' communication ranges.
 * 
 * @author Sam Macbeth
 *
 */
public class CommunicationRangeService extends EnvironmentService {

	/**
	 * @param sharedState
	 */
	protected CommunicationRangeService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
	}

	public double getAgentCommunicationRange(UUID participantID) {
		return (Double) this.sharedState.get("network.commrange", participantID).getValue();
	}

	/**
	 * Create the shared state required for this service.
	 * @param pid	{@link UUID} of the participant to create shared state for.
	 * @param range	{@link HasCommunicationRange} provider for this participant
	 * @return	{@link ParticipantSharedState} on the type that this service uses.
	 */
	public static ParticipantSharedState<Double> createSharedState(UUID pid, HasCommunicationRange range) {
		return new ParticipantSharedState<Double>("network.commrange", range.getCommunicationRange(), pid);
	}

}
