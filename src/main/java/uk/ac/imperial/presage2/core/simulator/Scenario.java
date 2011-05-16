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

package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

/**
 * 
 * <p>A Scenario describes the runtime components of a simulation which the
 * simulator must interact with. These comprise of the {@link Participant}s of the
 * system, the other {@link TimeDriven} elements, and the {@link Plugin}s. The Scenario
 * will likely have to construct other elements which the above require such as network
 * components and the environment.</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface Scenario {

	public Set<Participant> getParticipants();
	
	public Set<TimeDriven> getTimeDriven();
	
	public Set<Plugin> getPlugins();
	
	/**
	 * Get the time at which the simulation should finish
	 * @return {@link Time} representing the last time step of the simulation
	 */
	public Time getFinishTime();
	
}
