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
package uk.ac.imperial.presage2.core.cli.run;

/**
 * A {@link SimulationExecutor} is capable of executing simulations from their
 * ID.
 * 
 * @author Sam Macbeth
 * 
 */
public interface SimulationExecutor {

	/**
	 * Submits a simulation to be run on this executor.
	 * 
	 * @param simId
	 *            Id of the simulation to run.
	 * @throws InsufficientResourcesException
	 *             if the executor does not have enough resources to handle this
	 *             request and so refuses to fulfil it.
	 */
	public void run(long simId) throws InsufficientResourcesException;

	/**
	 * Get the number of simulations this executor is currently running.
	 * 
	 * @return no. of simulations currently running.
	 */
	public int running();

	/**
	 * Get the maximum number of simultaneously executing simulations this
	 * executor can handle.
	 * 
	 * @return max. no. of concurrent simulations.
	 */
	public int maxConcurrent();

	/**
	 * Enable or disable saving of execution logs to file.
	 * 
	 * @param saveLogs
	 */
	public void enableLogs(boolean saveLogs);

	/**
	 * Returns whether this executor will log output to file.
	 * 
	 * @return
	 */
	public boolean enableLogs();

	/**
	 * Set directory to which execution logs will be written.
	 * 
	 * @param logsDir
	 */
	public void setLogsDirectory(String logsDir);

	/**
	 * Get directory to which execution log will be written.
	 * 
	 * @return
	 */
	public String getLogsDirectory();

}
