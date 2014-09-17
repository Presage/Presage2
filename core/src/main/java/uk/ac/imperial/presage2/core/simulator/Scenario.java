/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.core.simulator;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * The scenario defines the entities to be run by the scheduler.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Scenario {

	/**
	 * Add an agent to the scenario
	 * 
	 * @param o
	 */
	public void addAgent(Object o);

	/**
	 * Add an object to the scenario
	 * 
	 * @param o
	 */
	public void addObject(Object o);

	/**
	 * Backwards compatibility for TimeDriven entities.
	 * 
	 * @param object
	 * @deprecated Use instead {@link #addObject(Object)} with {@link Step}
	 *             annotations.
	 */
	@Deprecated
	public void addTimeDriven(TimeDriven object);

	/**
	 * No-op as state engine is now handled directly by the simulator.
	 * 
	 * @param object
	 * @deprecated
	 */
	@Deprecated
	public void addEnvironment(TimeDriven object);

	/**
	 * Backwards compatibility for {@link Participant} entities.
	 * 
	 * @param agent
	 * @deprecated Use instead {@link #addAgent(Object)} with {@link Step}
	 *             annotations
	 */
	@Deprecated
	public void addParticipant(Participant agent);

}
