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

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * A {@link RunnableSimulation} for simulations which we define with a set of
 * {@link AbstractModule}s.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class InjectedSimulation extends RunnableSimulation {

	private Injector injector;

	private final Set<AbstractModule> modules = new HashSet<AbstractModule>();

	/**
	 * Create the simulation wrapper with some addition modules to use.
	 * 
	 * @param modules
	 */
	public InjectedSimulation(Set<AbstractModule> modules) {
		super();
		this.modules.addAll(modules);
	}

	public InjectedSimulation() {
		super();
	}

	/**
	 * Add an {@link AbstractModule} to the set we will use to create this
	 * simulation's {@link Scenario} and {@link Simulator}.
	 * 
	 * @param module
	 */
	final protected void addModule(AbstractModule module) {
		this.modules.add(module);
	}

	/**
	 * Add a set of {@link AbstractModule}s to the set we will use to create
	 * this simulation's {@link Scenario} and {@link Simulator}.
	 * 
	 * @param module
	 */
	final protected void addModules(Set<AbstractModule> modules) {
		this.modules.addAll(modules);
	}

	protected abstract Set<AbstractModule> getModules();

	final public void load() {
		this.addModules(getModules());
		// create scenario, simulator & injector
		AbstractModule[] marray = new AbstractModule[modules.size()];
		int i = 0;
		for (AbstractModule mod : modules) {
			marray[i] = mod;
			i++;
		}
		scenario = Scenario.Builder.createFromModules(marray);
		injector = Scenario.Builder.injector;
		injector.injectMembers(this);
		// start database
		if (database != null) {
			try {
				database.start();
			} catch (Exception e) {
				logger.fatal("Error starting database", e);
				throw new RuntimeException(e);
			}
		}
		simulator = injector.getInstance(Simulator.class);
		this.addToScenario(scenario);

		// add simulation to database
		if (storage != null) {
			storage.insertSimulation(this);
		}
		this.state = SimulationState.READY;
	}

	protected abstract void addToScenario(Scenario s);

	protected final Injector getInjector() {
		return injector;
	}

	@Override
	@Inject(optional = true)
	protected void setDatabase(DatabaseService database) {
		super.setDatabase(database);
	}

	@Override
	@Inject(optional = true)
	protected void setStorage(StorageService storage) {
		super.setStorage(storage);
	}

}
