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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * 
 * <p>
 * A Scenario describes the runtime components of a simulation which the
 * simulator must interact with. These comprise of the {@link Participant}s of
 * the system, the other {@link TimeDriven} elements, and the {@link Plugin}s.
 * The Scenario will likely have to construct other elements which the above
 * require such as network components and the environment.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public interface Scenario {

	public Set<Participant> getParticipants();

	public Set<TimeDriven> getTimeDriven();

	public Set<Plugin> getPlugins();

	public TimeDriven getEnvironment();

	/**
	 * Get the time at which the simulation should finish
	 * 
	 * @return {@link Time} representing the last time step of the simulation
	 */
	public Time getFinishTime();

	public abstract void addTimeDriven(TimeDriven t);

	public abstract void addPlugin(Plugin p);

	public abstract void addParticipant(Participant p);

	public void addEnvironment(TimeDriven e);

	static class Builder {

		public static Injector injector = null;

		/**
		 * <p>
		 * Create a {@link Scenario} from a given array of
		 * {@link AbstractModule}
		 * </p>
		 * 
		 * <p>
		 * We store the {@link Injector} created in
		 * {@link Scenario.Builder#injector} for others who wish to use it.
		 * </p>
		 * 
		 * <p>
		 * For other objects created outside of this method and requiring member
		 * injection use {@link #injectMembers(Object)}.
		 * </p>
		 * 
		 * @param modules
		 * @return {@link Scenario}
		 */
		public static Scenario createFromModules(AbstractModule... modules) {
			final Set<AbstractModule> moduleSet = new HashSet<AbstractModule>(
					Arrays.asList(modules));
			moduleSet.add(new AbstractModule() {
				@Override
				protected void configure() {
					bind(Scenario.class).to(ScenarioBuilder.class).in(
							Singleton.class);
				}
			});
			Scenario.Builder.injector = Guice.createInjector(moduleSet);
			return Scenario.Builder.injector.getInstance(Scenario.class);
		}

		/**
		 * Wrapper for {@link Injector#injectMembers(Object)}s.
		 * 
		 * @param obj
		 */
		public static <T extends Object> T injectMembers(T obj) {
			Scenario.Builder.injector.injectMembers(obj);
			return obj;
		}
	}
}
