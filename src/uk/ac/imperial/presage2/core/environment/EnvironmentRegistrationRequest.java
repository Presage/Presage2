/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

import java.util.UUID;

/**
 * @author Sam Macbeth
 *
 */
public class EnvironmentRegistrationRequest {

	protected UUID participantID;

	public EnvironmentRegistrationRequest(UUID participantID) {
		super();
		this.participantID = participantID;
	}
	
}
