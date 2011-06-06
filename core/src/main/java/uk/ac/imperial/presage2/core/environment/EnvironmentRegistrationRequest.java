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

import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * A request to register with the environment.
 * 
 * @author Sam Macbeth
 *
 */
public class EnvironmentRegistrationRequest {

	protected UUID participantID;
	
	/**
	 * We may need to the Participant in order to create some {@link EnvironmentService}s.
	 */
	protected Participant participant;
	
	/**
	 * Set of sharedstate which we share with the environment.
	 */
	protected Set<ParticipantSharedState<?>> sharedState;

	/**
	 * Construct an EnvironmentRegistrationRequest without sharedstate.
	 * @param participantID
	 * @param participant
	 */
	public EnvironmentRegistrationRequest(UUID participantID,
			Participant participant) {
		super();
		this.participantID = participantID;
		this.participant = participant;
	}

	/**
	 * @param participantID
	 * @param participant
	 * @param sharedState
	 */
	public EnvironmentRegistrationRequest(UUID participantID,
			Participant participant, Set<ParticipantSharedState<?>> sharedState) {
		super();
		this.participantID = participantID;
		this.participant = participant;
		this.sharedState = sharedState;
	}

	/**
	 * @return the sharedState
	 */
	public Set<ParticipantSharedState<?>> getSharedState() {
		return sharedState;
	}

	/**
	 * @param sharedState the sharedState to set
	 */
	public void setSharedState(Set<ParticipantSharedState<?>> sharedState) {
		this.sharedState = sharedState;
	}

	/**
	 * @return the participantID
	 */
	public UUID getParticipantID() {
		return participantID;
	}

	/**
	 * @return the participant
	 */
	public Participant getParticipant() {
		return participant;
	}
	
}
