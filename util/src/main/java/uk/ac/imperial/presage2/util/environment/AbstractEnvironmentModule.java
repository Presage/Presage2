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
package uk.ac.imperial.presage2.util.environment;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class AbstractEnvironmentModule extends AbstractModule {

	final Class<? extends AbstractEnvironment> environmentImplementation;

	final Set<Class<? extends EnvironmentService>> environmentServices;

	final Set<Class<? extends ActionHandler>> actionHandlers;

	/**
	 * <p>
	 * Module to bind components required for an {@link AbstractEnvironment}.
	 * </p>
	 * 
	 * @param environmentImplementation
	 *            Environment implementation to use.
	 * @param environmentServices
	 *            Set of global {@link EnvironmentService}s to use.
	 * @param actionHandlers
	 *            Set of {@link ActionHandler}s to use.
	 */
	public AbstractEnvironmentModule(
			Class<? extends AbstractEnvironment> environmentImplementation,
			Set<Class<? extends EnvironmentService>> environmentServices,
			Set<Class<? extends ActionHandler>> actionHandlers) {
		super();
		this.environmentImplementation = environmentImplementation;
		this.environmentServices = environmentServices;
		this.actionHandlers = actionHandlers;
		this.processServiceDependencies();
	}

	/**
	 * Takes the sets of {@link EnvironmentService}s and {@link ActionHandler}s,
	 * examines their {@link ServiceDependencies} and adds them to the set of
	 * {@link EnvironmentService}s
	 */
	final private void processServiceDependencies() {
		final Set<Class<? extends EnvironmentService>> dependencies = new HashSet<Class<? extends EnvironmentService>>();
		for (Class<? extends EnvironmentService> service : environmentServices) {
			if (service.isAnnotationPresent(ServiceDependencies.class)) {
				for (Class<? extends EnvironmentService> dep : service
						.getAnnotation(ServiceDependencies.class).value()) {
					dependencies.add(dep);
				}
			}
		}
		for (Class<? extends ActionHandler> handler : actionHandlers) {
			if (handler.isAnnotationPresent(ServiceDependencies.class)) {
				for (Class<? extends EnvironmentService> dep : handler
						.getAnnotation(ServiceDependencies.class).value()) {
					dependencies.add(dep);
				}
			}
		}
		this.environmentServices.addAll(dependencies);
	}

	@Override
	protected void configure() {
		// bind AbstractEnvironment interfaces
		bind(EnvironmentConnector.class).to(AbstractEnvironment.class);
		bind(EnvironmentServiceProvider.class).to(AbstractEnvironment.class);
		bind(EnvironmentSharedStateAccess.class).to(AbstractEnvironment.class);

		// bind Singleton implementation to AbstractEnvironment
		bind(AbstractEnvironment.class).to(environmentImplementation).in(
				Singleton.class);

		// global environment services
		Multibinder<EnvironmentService> serviceBinder = Multibinder
				.newSetBinder(binder(), EnvironmentService.class);
		for (Class<? extends EnvironmentService> service : environmentServices) {

			serviceBinder.addBinding().to(service);
		}

		// action handlers
		Multibinder<ActionHandler> actionBinder = Multibinder.newSetBinder(
				binder(), ActionHandler.class);
		for (Class<? extends ActionHandler> handler : actionHandlers) {
			actionBinder.addBinding().to(handler);
		}
	}

}
