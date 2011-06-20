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

package uk.ac.imperial.presage2.core.environment;

import java.util.Map;

import com.google.inject.Inject;

/**
 * <p>An EnvironmentService provides a high level service to a Participant and/or
 * the NetworkController by accessing the raw data in the environment shared state.</p>
 * 
 * <p>This provides an abstraction layer to the shared state as well as protection of shared
 * state data (performing object copys when necessary) and access limitations as required</p>
 */
abstract public class EnvironmentService {

	protected EnvironmentSharedStateAccess sharedState;

	/**
	 * @param sharedState
	 */
	@Inject
	protected EnvironmentService(EnvironmentSharedStateAccess sharedState) {
		super();
		this.sharedState = sharedState;
	}

	/**
	 * Initialise global shared state required by this environmentservice (optional).
	 * @param globalSharedState
	 */
	public void initialise(Map<String, SharedState<?>> globalSharedState) {

	}

	/**
	 * Called when a participant is registered (optional).
	 * @param req
	 * @param globalSharedState
	 */
	public void registerParticipant(EnvironmentRegistrationRequest req, Map<String, SharedState<?>> globalSharedState) {

	}

}
