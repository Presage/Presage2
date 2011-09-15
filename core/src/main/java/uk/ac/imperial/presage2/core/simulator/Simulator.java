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
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.event.EventBus;

import com.google.inject.Inject;

/**
 * @author Sam Macbeth
 * 
 */
public abstract class Simulator implements ThreadPool {

	/**
	 * The Scenario to simulate
	 */
	protected Scenario scenario;
	protected final Time time;
	protected final SimTime simTime;
	protected DatabaseService database;
	protected EventBus eventBus;

	@Inject
	public Simulator(Scenario scenario, Time t, EventBus eventBus) {
		this.scenario = scenario;
		this.time = t;
		this.simTime = new SimTime(t);
		this.eventBus = eventBus;
	}

	@Inject(optional = true)
	public void setDatabaseService(DatabaseService db) {
		database = db;
	}

	/**
	 * <p>
	 * Start running this simulation.
	 * </p>
	 * 
	 * <p>
	 * Will run {@link #initialise()}, {@link #run()} and {@link #complete()} in
	 * order.
	 * </p>
	 */
	public void start() {
		this.initialise();
		this.run();
		this.complete();
	}

	/**
	 * Initialise simulation components.
	 */
	public abstract void initialise();

	/**
	 * Run the core simulation
	 */
	public abstract void run();

	/**
	 * Complete post simulation actions and tidy up.
	 */
	public abstract void complete();

	public Time getCurrentSimulationTime() {
		return time.clone();
	}

	public abstract void shutdown();

}
