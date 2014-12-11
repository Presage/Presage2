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

import java.util.Set;

import uk.ac.imperial.presage2.core.simulator.RunnableSimulation.InjectedObjects;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation.RuntimeScenario;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;

public class ScenarioModule extends AbstractModule {

	final RuntimeScenario scenario;
	final Set<DeclaredParameter> parameters;
	final Scheduler scheduler;

	ScenarioModule(RuntimeScenario scenario, Set<DeclaredParameter> parameters,
			Scheduler scheduler) {
		super();
		this.scenario = scenario;
		this.parameters = parameters;
		this.scheduler = scheduler;
	}

	@Override
	protected void configure() {
		bind(Scenario.class).toInstance(scenario);
		bind(Scheduler.class).toInstance(scheduler);
		for (DeclaredParameter p : parameters) {
			install(p.handler.getBinding(p));
		}
		Multibinder<Object> mb = Multibinder.newSetBinder(binder(),
				Object.class, InjectedObjects.class);
		for (Object o : scenario.objects) {
			mb.addBinding().toInstance(o);
		}
		for (Class<?> c : scenario.classes) {
			mb.addBinding().to(c);
		}
	}

	public static void addObjects(Binder binder, Object... objects) {
		Multibinder<Object> mb = Multibinder.newSetBinder(binder, Object.class,
				InjectedObjects.class);
		for (Object o : objects) {
			mb.addBinding().toInstance(o);
		}
	}

	public static void addObjectClasses(Binder binder,
			Class<?>... objects) {
		Multibinder<Object> mb = Multibinder.newSetBinder(binder, Object.class,
				InjectedObjects.class);
		for (Class<? extends Object> o : objects) {
			mb.addBinding().to(o);
		}
	}

}
