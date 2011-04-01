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

import java.util.UUID;

import uk.ac.imperial.presage2.core.Action;

/**
 * This interface gives the API for basic access to the 
 * Environment, allowing Participants to register, deregister
 * and act on the environment.
 * 
 * @author Sam Macbeth
 *
 */
public interface EnvironmentConnector {

	/**
	 * <p>Registers a {@link uk.ac.imperial.presage2.core.participant.Participant} with the simulation environment.</p>
	 * 
	 * @param request
	 * @return
	 */
	public EnvironmentRegistrationResponse register(EnvironmentRegistrationRequest request);
	
	/**
	 * <p>Performs an action on the environment</p>
	 * @param action
	 * @param actor
	 * @param authkey
	 */
	public void act(Action action, UUID actor, UUID authkey) throws ActionHandlingException;
	
	/**
	 * <p>Deregisters a participant with the environment.</p>
	 * @param participantID
	 * @param authkey
	 */
	public void deregister(UUID participantID, UUID authkey);
	
}
