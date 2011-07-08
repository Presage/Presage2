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

import uk.ac.imperial.presage2.core.Time;

/**
 * <p>
 * A RunnableSimulation is a wrapper for a parameterised experiment we can run.
 * </p>
 * 
 * <p>
 * It gives method to monitor the progress and state of the simulation from a
 * controlling program.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public interface RunnableSimulation extends Runnable {

	public enum SimulationState {
		LOADING, READY, INITIALISING, RUNNING, PAUSED, STOPPED, FINISHING, COMPLETE
	}

	/**
	 * Inform the simulation that parameters have been initialised and that it
	 * should build itself ready to run.
	 */
	public void load();

	/**
	 * Get the simulation's current state.
	 * 
	 * @return {@link SimulationState}
	 */
	public SimulationState getState();

	/**
	 * Get the current time in the underlying running simulation.
	 * 
	 * @return {@link Time}
	 */
	public Time getCurrentSimulationTime();

	/**
	 * Get the time the underlying simulation should finish.
	 * 
	 * @return {@link Time}
	 */
	public Time getSimulationFinishTime();

	/**
	 * Get the percentage completion of the simulation.
	 * 
	 * @return
	 */
	public int getSimluationPercentComplete();

	/**
	 * Get the underlying scenario this simulation is running.
	 * 
	 * @return {@link Scenario}
	 */
	public Scenario getScenario();

	/**
	 * Get the simulator running this simulation.
	 * 
	 * @return {@link Simulator}
	 */
	public Simulator getSimulator();

	/**
	 * Run this simulation.
	 */
	public void run();

}
