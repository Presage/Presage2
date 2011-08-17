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
package uk.ac.imperial.presage2.core.db;

import java.util.UUID;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.SimulationFactory;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

/**
 * The graphDB is an API for using a graph-based database to store simulation
 * data.
 * 
 * @author Sam Macbeth
 * 
 */
public interface GraphDB {

	/**
	 * Get the {@link SimulationFactory} instance.
	 * 
	 * @return
	 */
	public SimulationFactory getSimulationFactory();

	/**
	 * Get the {@link PersistentSimulation} for the currently running
	 * simulation.
	 * 
	 * @return
	 */
	public PersistentSimulation getSimulation();

	/**
	 * Set the {@link PersistentSimulation} to use.
	 * 
	 * @param sim
	 */
	public void setSimulation(PersistentSimulation sim);

	/**
	 * Get the {@link PersistentAgent} for this simulation with given UUID.
	 * 
	 * @param agentID
	 * @return
	 */
	public PersistentAgent getAgent(UUID agentID);

	/**
	 * Get the {@link TransientAgentState} associated with agentID at a given
	 * time.
	 * 
	 * @param agentID
	 * @param time
	 * @return
	 */
	public TransientAgentState getAgentState(UUID agentID, int time);

	/**
	 * Start a {@link Transaction} on the graph db.
	 * 
	 * @return
	 */
	public Transaction startTransaction();

}
