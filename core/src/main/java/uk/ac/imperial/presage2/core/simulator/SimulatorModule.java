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

import com.google.inject.AbstractModule;

/**
 * <p>
 * Global simulator level guice bindings
 * </p>
 * 
 * 
 * @author Sam Macbeth
 * 
 */
public class SimulatorModule extends AbstractModule {

	final private Class<? extends Simulator> simulatorImplementation;

	private SimulatorModule(Class<? extends Simulator> simulatorImplementation) {
		super();
		this.simulatorImplementation = simulatorImplementation;
	}

	@Override
	protected void configure() {
		bind(Simulator.class).to(simulatorImplementation);
	}

	public static SimulatorModule singleThreadedSimulator() {
		return new SimulatorModule(SingleThreadedSimulator.class);
	}

	public static SimulatorModule multiThreadedSimulator(final int threads) {
		return new SimulatorModule(MultiThreadedSimulator.class) {
			@Override
			protected void configure() {
				super.configure();
				bind(Integer.class).annotatedWith(Threads.class).toInstance(
						threads);
			}
		};
	}

}
