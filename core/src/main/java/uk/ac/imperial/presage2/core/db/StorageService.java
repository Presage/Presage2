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

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.db.Table.TableBuilder;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;

/**
 * The StorageService is a high level interface to database access for storing
 * simulation data. Allows for {@link Table}s to be constructed which in turn
 * allow data to be inserted into the database.
 * 
 * @author Sam Macbeth
 * 
 */
@Deprecated
public interface StorageService {

	/**
	 * Get the current simulation ID
	 * 
	 * @return
	 */
	public long getSimulationId();

	public void setSimulationId(long id);

	/**
	 * Get the current simulation time as an int.
	 * 
	 * @return
	 */
	public int getTime();

	public void setTime(Time t);

	/**
	 * Start building a Table.
	 * 
	 * @param tableName
	 * @return {@link TableBuilder}
	 */
	public TableBuilder buildTable(String tableName);

	/**
	 * Insert the {@link RunnableSimulation} into the database.
	 * 
	 * @param sim
	 */
	public void insertSimulation(RunnableSimulation sim);

	/**
	 * Update the {@link RunnableSimulation} entry in the database to reflect
	 * any changes in it's state.
	 */
	public void updateSimulation();

}
