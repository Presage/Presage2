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

/**
 * 
 * This is the interface used by the simulator to interact with agents. All
 * participants must implement this interface.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Participant {

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
	 * Adds a new input to be processed by this participant.
	 * 
	 * @param input
	 */
	public void enqueueInput(Object input);

	/**
	 * Adds multiple new inputs to be processed by this participant.
	 * 
	 * @param inputs
	 */
	public void enqueueInput(Collection<? extends Object> inputs);

}
