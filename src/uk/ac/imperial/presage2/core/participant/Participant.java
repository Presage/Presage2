/**
 * 
 */
package uk.ac.imperial.presage2.core.participant;

import java.util.UUID;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.messaging.Input;

/**
 * 
 * This is the interface used by the simulator to interact with
 * agents. All participants must implement this interface.
 * 
 * @author Sam Macbeth
 *
 */
public interface Participant extends TimeDriven {

	/**
	 * Returns the participant's unique ID.
	 * @return This participant's unique UUID
	 */
	public UUID getID();
	
	/**
	 * Returns a unique string identifier for this participant.
	 * This is used purely for a human readable identifier of the
	 * agent so the unique requirement is not mandatory. However it
	 * is convenient.
	 * @return unique string identifier for this participant
	 */
	public String getName();
	
	/**
	 * Returns the agent's current perception of the simulation time.
	 * @return agent's current perception of the simulation time
	 */
	public Time getTime();
	
	/**
	 * Called by the simulator after creating your agent.
	 * Allows you to initialise the agent before simulation cycle starts
	 */
	public void initialise() throws ParticipantInitialisationException;
	
	/**
	 * Analogous to step. 
	 * @see uk.ac.imperial.presage2.core.participant.Participant#step()
	 */
	@Deprecated
	public void execute();
	
	/**
	 * Called once per simulation cycle. Gives the agent time to process
	 * inputs, send message and perform actions.
	 */
	public void step() throws ParticipantRunTimeException;
	
	/**
	 * Adds a new input to be processed by this participant.
	 * @param input
	 */
	public void enqueueInput(Input input);
	
	
}
