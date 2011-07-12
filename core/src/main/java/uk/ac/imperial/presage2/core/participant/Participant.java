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

package uk.ac.imperial.presage2.core.participant;

import java.util.Collection;
import java.util.UUID;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.messaging.Input;

/**
 * 
 * This is the interface used by the simulator to interact with agents. All
 * participants must implement this interface.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Participant extends TimeDriven {

	/**
	 * Returns the participant's unique ID.
	 * 
	 * @return This participant's unique UUID
	 */
	public UUID getID();

	/**
	 * Returns a unique string identifier for this participant. This is used
	 * purely for a human readable identifier of the agent so the unique
	 * requirement is not mandatory. However it is convenient.
	 * 
	 * @return unique string identifier for this participant
	 */
	public String getName();

	/**
	 * Returns the agent's current perception of the simulation time.
	 * 
	 * @return agent's current perception of the simulation time
	 */
	public Time getTime();

	/**
	 * Called by the simulator after creating your agent. Allows you to
	 * initialise the agent before simulation cycle starts
	 */
	public void initialise();

	/**
	 * Called once per simulation cycle. Gives the agent time to process inputs,
	 * send message and perform actions.
	 * 
	 * @deprecated Use {@link TimeDriven#incrementTime()} now.
	 */
	@Deprecated
	public void execute();

	/**
	 * Called at the end of the simulation. A chance for the Participant to tidy
	 * itself up before the garbage collector comes for it...
	 */
	public void onSimulationComplete();

	/**
	 * Adds a new input to be processed by this participant.
	 * 
	 * @param input
	 */
	public void enqueueInput(Input input);

	/**
	 * Adds multiple new inputs to be processed by this participant.
	 * 
	 * @param inputs
	 */
	public void enqueueInput(Collection<? extends Input> inputs);

}
