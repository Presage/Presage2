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
package uk.ac.imperial.presage2.util.network;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Static factory for {@link AbstractModule}s which bind network interfaces.
 * 
 * @author Sam Macbeth
 * 
 */
public class NetworkModule extends AbstractModule {

	private Set<Class<? extends NetworkConstraint>> constraints = new HashSet<Class<? extends NetworkConstraint>>();

	final NetworkModule withConstraints(
			Set<Class<? extends NetworkConstraint>> constraints) {
		this.constraints.addAll(constraints);
		return this;
	}

	public static NetworkModule fullyConnectedNetworkModule() {
		return new NetworkModule();
	}

	public static NetworkModule constrainedNetworkModule(
			final Set<Class<? extends NetworkConstraint>> constraints) {
		return new NetworkModule().withConstraints(constraints);
	}

	@Override
	protected void configure() {
		// Bind MessageHandler as action hander
		Multibinder.newSetBinder(binder(), ActionHandler.class).addBinding()
				.to(MessageHandler.class);
		// Bind MessageHandler as global environment service
		Multibinder.newSetBinder(binder(), EnvironmentService.class)
				.addBinding().to(MessageHandler.class);
		// TODO: Bind NetworkConnect as participant environment service.
		// Currently can't be done as binder is hidden by
		// AbstractEnvironmentModule

		if (this.constraints.size() > 0) {
			Multibinder<NetworkConstraint> constraintBinder = Multibinder
					.newSetBinder(binder(), NetworkConstraint.class);
			Multibinder<EnvironmentService> serviceBinder = Multibinder
					.newSetBinder(binder(), EnvironmentService.class);
			for (Class<? extends NetworkConstraint> c : constraints) {
				constraintBinder.addBinding().to(c);
				if (c.isAnnotationPresent(ServiceDependencies.class)) {
					for (Class<? extends EnvironmentService> dep : c
							.getAnnotation(ServiceDependencies.class).value()) {
						serviceBinder.addBinding().to(dep);
					}
				}
			}
		}
	}

}
