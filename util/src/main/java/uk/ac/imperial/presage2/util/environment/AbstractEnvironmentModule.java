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
import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.core.participant.Participant;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

public class AbstractEnvironmentModule extends AbstractModule {

	final Class<? extends AbstractEnvironment> environmentImplementation;

	final Set<Class<? extends EnvironmentService>> environmentServices;
	final Set<Class<? extends EnvironmentService>> participantEnvironmentServices;
	final Set<Class<? extends EnvironmentService>> participantGlobalEnvironmentServices;

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
		this.participantEnvironmentServices = new HashSet<Class<? extends EnvironmentService>>();
		this.participantGlobalEnvironmentServices = new HashSet<Class<? extends EnvironmentService>>();
	}

	/**
	 * <p>
	 * Module to bind components required for an {@link AbstractEnvironment}.
	 * </p>
	 * 
	 * @param environmentImplementation
	 *            Environment implementation to use.
	 */
	public AbstractEnvironmentModule(
			Class<? extends AbstractEnvironment> environmentImplementation) {
		this(environmentImplementation,
				new HashSet<Class<? extends EnvironmentService>>(),
				new HashSet<Class<? extends ActionHandler>>());
	}

	/**
	 * Add global environment services to be bound to the environment's service
	 * provider.
	 * 
	 * @param services
	 *            {@link EnvironmentService} classes to add
	 * @return this
	 */
	public AbstractEnvironmentModule addGlobalEnvironmentServices(
			Class<? extends EnvironmentService>... services) {
		for (Class<? extends EnvironmentService> s : services) {
			this.environmentServices.add(s);
		}
		return this;
	}

	/**
	 * Add a global environment service to be bound to the environment's service
	 * provider.
	 * 
	 * @param service
	 *            {@link EnvironmentService} class to add
	 * @return this
	 */
	public AbstractEnvironmentModule addGlobalEnvironmentService(
			Class<? extends EnvironmentService> service) {
		this.environmentServices.add(service);
		return this;
	}

	/**
	 * Add {@link ActionHandler}s to be bound to the environment.
	 * 
	 * @param handlers
	 *            {@link ActionHandler}s to bind.
	 * @return this
	 */
	public AbstractEnvironmentModule addActionHandlers(
			Class<? extends ActionHandler>... handlers) {
		for (Class<? extends ActionHandler> h : handlers) {
			this.actionHandlers.add(h);
		}
		return this;
	}

	/**
	 * Add an {@link ActionHandler} to be bound to the environment.
	 * 
	 * @param handler
	 *            {@link ActionHandler} to bind.
	 * @return this.
	 */
	public AbstractEnvironmentModule addActionHandler(
			Class<? extends ActionHandler> handler) {
		this.actionHandlers.add(handler);
		return this;
	}

	/**
	 * Add {@link EnvironmentService}s which will be provided to
	 * {@link Participant}s on registration with the environment.
	 * 
	 * @param services
	 *            {@link EnvironmentService}s to add.
	 * @return this
	 */
	public AbstractEnvironmentModule addParticipantEnvironmentServices(
			Class<? extends EnvironmentService>... services) {
		for (Class<? extends EnvironmentService> s : services) {
			this.participantEnvironmentServices.add(s);
		}
		return this;
	}

	/**
	 * Add an {@link EnvironmentService} which will be provided to
	 * {@link Participant}s on registration with the environment.
	 * 
	 * @param services
	 *            {@link EnvironmentService} to add.
	 * @return this
	 */
	public AbstractEnvironmentModule addParticipantEnvironmentService(
			Class<? extends EnvironmentService> service) {
		this.participantEnvironmentServices.add(service);
		return this;
	}

	/**
	 * Add global {@link EnvironmentService}s which will be provided to
	 * {@link Participant}s on registration with the environment.
	 * 
	 * @param services
	 *            {@link EnvironmentService}s to add.
	 * @return this
	 */
	public AbstractEnvironmentModule addParticipantGlobalEnvironmentServices(
			Class<? extends EnvironmentService>... services) {
		for (Class<? extends EnvironmentService> s : services) {
			this.environmentServices.add(s);
			this.participantGlobalEnvironmentServices.add(s);
		}
		return this;
	}

	/**
	 * Add a global {@link EnvironmentService} which will be provided to
	 * {@link Participant}s on registration with the environment.
	 * 
	 * @param services
	 *            {@link EnvironmentService} to add.
	 * @return this
	 */
	public AbstractEnvironmentModule addParticipantGlobalEnvironmentService(
			Class<? extends EnvironmentService> service) {
		this.environmentServices.add(service);
		this.participantGlobalEnvironmentServices.add(service);
		return this;
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
		// resolve any additional service dependencies
		this.processServiceDependencies();

		// bind AbstractEnvironment interfaces
		bind(EnvironmentConnector.class).to(AbstractEnvironment.class);
		bind(EnvironmentServiceProvider.class).to(AbstractEnvironment.class);
		bind(SharedStateStorage.class).to(MappedSharedState.class);
		bind(EnvironmentSharedStateAccess.class).to(MappedSharedState.class);
		bind(MappedSharedState.class).in(Singleton.class);

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

		// participant environment services
		Multibinder<Class<? extends EnvironmentService>> participantServiceBinder = Multibinder
				.newSetBinder(binder(),
						new TypeLiteral<Class<? extends EnvironmentService>>() {
						}, ParticipantEnvironmentServices.class);
		for (Class<? extends EnvironmentService> service : participantEnvironmentServices) {
			participantServiceBinder.addBinding().toInstance(service);
		}

		// participant global environment services
		Multibinder<Class<? extends EnvironmentService>> participantGlobalServiceBinder = Multibinder
				.newSetBinder(binder(),
						new TypeLiteral<Class<? extends EnvironmentService>>() {
						}, ParticipantGlobalEnvironmentServices.class);
		for (Class<? extends EnvironmentService> service : participantGlobalEnvironmentServices) {
			participantGlobalServiceBinder.addBinding().toInstance(service);
		}
	}

}
