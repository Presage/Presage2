/**
 * 
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
