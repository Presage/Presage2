/**
 * 
 */
package org.imperial.isn.presage2.core.participant;

/**
 * 
 * This is the interface used by the simulator to interact with
 * agents. All participants must implement this interface.
 * 
 * @author Sam Macbeth
 *
 */
public interface Participant {

	/**
	 * Called by the simulator after creating your agent.
	 * Allows you to initialise the agent before simulation cycle starts
	 */
	public void initialise();
	
	/**
	 * Analogous to step. 
	 * @see org.imperial.isn.presage2.core.participant.Participant#step()
	 */
	@Deprecated
	public void execute();
	
	/**
	 * Called once per simulation cycle. Gives the agent time to process
	 * inputs, send message and perform actions.
	 */
	public void step();
	
	/**
	 * Adds a new input to be processed by this participant.
	 * @param input
	 */
	public void enqueueInput(Input input);
	
	
}
