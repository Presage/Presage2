/**
 * 
 */
package org.imperial.isn.presage2.core.environment;

import java.util.UUID;

import org.imperial.isn.presage2.core.Action;

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
	 * <p>Registers a {@link org.imperial.isn.presage2.core.participant.Participant} with the simulation environment.</p>
	 * 
	 * @param request
	 * @return
	 */
	public EnvironmentRegistrationRespone register(EnvironmentRegistrationRequest request);
	
	/**
	 * <p>Performs an action on the environment</p>
	 * @param action
	 * @param actor
	 * @param authkey
	 */
	public void act(Action action, UUID actor, UUID authkey);
	
	/**
	 * <p>Deregisters a participant with the environment.</p>
	 * @param participantID
	 * @param authkey
	 */
	public void deregister(UUID participantID, UUID authkey);
	
}
