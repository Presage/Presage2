package uk.ac.imperial.presage2.util.environment;

import java.util.Set;

import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;

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
		;

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
