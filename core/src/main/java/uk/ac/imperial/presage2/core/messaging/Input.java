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

package uk.ac.imperial.presage2.core.messaging;

import uk.ac.imperial.presage2.core.Time;

/**
 * 
 * An input is something which can be queued at an agent's input queue and
 * processed by the agent. This is the base level of agent-agent and
 * agent-environment interaction.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Input {

	/**
	 * Get the FIPA performative of this Input (if applicable).
	 * 
	 * @return {@link uk.ac.imperial.presage2.core.messaging.Performative}
	 */
	public Performative getPerformative();

	/**
	 * Get the timestamp of this Input.
	 * 
	 * @return {@link Time}
	 */
	public Time getTimestamp();

	/**
	 * set the timestamp of this Input
	 * 
	 * @param t
	 *            {@link Time} to set.
	 */
	public void setTimestamp(Time t);

	/**
	 * Get the type of this Input
	 * 
	 * @return
	 */
	public String getType();

}
