/**
 * 
 */
package uk.ac.imperial.presage2.core.messaging;

import uk.ac.imperial.presage2.core.Time;

/**
 * 
 * An input is something which can be queued at an agent's input queue and processed by the agent.
 * This is the base level of agent-agent and agent-environment interaction. 
 * 
 * @author Sam Macbeth
 *
 */
public interface Input {

	/**
	 * Get the FIPA performative of this Input (if applicable).
	 * @return {@link uk.ac.imperial.presage2.core.messaging.Performative}
	 */
	public Performative getPerformative();
	
	/**
	 * Get the timestamp of this Input.
	 * @return {@link Time}
	 */
	public Time getTimestamp();
	
	/**
	 * set the timestamp of this Input
	 * @param Time to set.
	 */
	public void setTimestamp(Time t);
	
}
