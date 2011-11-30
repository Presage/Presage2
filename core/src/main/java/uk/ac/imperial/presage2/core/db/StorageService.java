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

import java.util.List;
import java.util.UUID;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.SimulationFactory;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;

/**
 * A service to provide storage of simulation data.
 * 
 * @author Sam Macbeth
 * 
 */
public interface StorageService {

	/**
	 * Get the {@link SimulationFactory} instance.
	 * 
	 * @return
	 */
	@Deprecated
	public SimulationFactory getSimulationFactory();

	/**
	 * Create a {@link PersistentSimulation} for the given parameters and set it
	 * as the current simulation
	 * 
	 * @param name
	 *            name of the simulation
	 * @param classname
	 *            name of the simulation's main class
	 * @param state
	 *            current state of the simulation
	 * @param finishTime
	 *            number of simulation cycles in this sim
	 * @return A {@link PersistentSimulation} object relating to the simulation
	 *         entry created in the database.
	 */
	public PersistentSimulation createSimulation(String name, String classname, String state,
			int finishTime);

	/**
	 * Get the {@link PersistentSimulation} for the currently running
	 * simulation.
	 * 
	 * @return
	 */
	public PersistentSimulation getSimulation();

	/**
	 * Get a {@link PersistentSimulation} which corresponds to the given
	 * simulation ID.
	 * 
	 * @param id
	 * @return
	 */
	public PersistentSimulation getSimulationById(long id);

	/**
	 * 
	 * @return A List of the IDs of simulations kept in this storage medium.
	 */
	public List<Long> getSimulations();

	/**
	 * Set the {@link PersistentSimulation} to use.
	 * 
	 * @param sim
	 */
	public void setSimulation(PersistentSimulation sim);

	/**
	 * Create a new {@link PersistentAgent} for this simulation.
	 * 
	 * @param agentID
	 * @param name
	 * @return
	 */
	public PersistentAgent createAgent(UUID agentID, String name);

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
	 * @deprecated
	 */
	public Transaction startTransaction();

}
