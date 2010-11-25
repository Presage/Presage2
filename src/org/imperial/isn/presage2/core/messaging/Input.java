/**
 * 
 */
package org.imperial.isn.presage2.core.messaging;

import org.imperial.isn.presage2.core.Time;

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
	 * @return
	 */
	public Performative getPerformative();
	
	/**
	 * Get the timestamp of this Input.
	 * @return
	 */
	public Time getTimestamp();
	
	/**
	 * set the timestamp of this Input
	 * @param t
	 */
	public void setTimestamp(Time t);
	
}
