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
package uk.ac.imperial.presage2.util.location;

import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;

/**
 * An {@link EnvironmentService} to provide information
 * on the locations of agents.
 * 
 * @author Sam Macbeth
 *
 */
public class LocationService extends EnvironmentService {

	/**
	 * @param sharedState
	 */
	public LocationService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
	}

	/**
	 * Get the location of a given agent specified by it's participant UUID.
	 * @param participantID {@link UUID} of participant to look up
	 * @return	{@link Location} of participant.
	 * @throws CannotSeeAgent
	 */
	public Location getAgentLocation(UUID participantID) throws CannotSeeAgent {
		return (Location) this.sharedState.get("util.location", participantID).getValue();
	}

}
