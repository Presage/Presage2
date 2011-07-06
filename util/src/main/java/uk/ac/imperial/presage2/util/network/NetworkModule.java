package uk.ac.imperial.presage2.util.network;

import java.util.Set;

import uk.ac.imperial.presage2.core.network.*;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * Static factory for {@link AbstractModule}s which bind network interfaces.
 * @author Sam Macbeth
 *
 */
public final class NetworkModule {

	/**
	 * Module to bind a fully connected network, where every agent can message
	 * every other agent with no limitations.
	 * 
	 * @return {@link AbstractModule} binding network interfaces.
	 */
	public static AbstractModule fullyConnectedNetworkModule() {
		return new AbstractModule() {
			@Override
			protected void configure() {
				install(new FactoryModuleBuilder().implement(
						NetworkConnector.class, BasicNetworkConnector.class)
						.build(NetworkConnectorFactory.class));
				bind(NetworkChannel.class).to(NetworkController.class).in(
						Singleton.class);
				install(new FactoryModuleBuilder()
						.build(NetworkAddressFactory.class));
			}
		};
	}

	/**
	 * Module to bind a constrained network. This uses a
	 * {@link ConstrainedNetworkController} and a set of
	 * {@link NetworkConstraint}s to limit message passing in some way.
	 * 
	 * @param constraints
	 *            {@link NetworkConstraint}s to use.
	 * @return {@link AbstractModule} binding network interfaces.
	 */
	public static AbstractModule constrainedNetworkModule(
			final Set<Class<? extends NetworkConstraint>> constraints) {
		return new AbstractModule() {
			@Override
			protected void configure() {
				install(new FactoryModuleBuilder().implement(
						NetworkConnector.class, BasicNetworkConnector.class)
						.build(NetworkConnectorFactory.class));
				bind(NetworkChannel.class).to(
						ConstrainedNetworkController.class).in(Singleton.class);
				install(new FactoryModuleBuilder()
						.build(NetworkAddressFactory.class));

				// network constraints
				Multibinder<NetworkConstraint> constraintBinder = Multibinder
						.newSetBinder(binder(), NetworkConstraint.class);
				for (Class<? extends NetworkConstraint> c : constraints) {
					constraintBinder.addBinding().to(c);
				}
			}
		};
	}

}
