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
package uk.ac.imperial.presage2.util.network;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.network.*;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * Static factory for {@link AbstractModule}s which bind network interfaces.
 * 
 * @author Sam Macbeth
 * 
 */
public final class NetworkModule extends AbstractModule {

	private Class<? extends NetworkConnector> connector;
	private Class<? extends NetworkController> controller;
	private Set<Class<? extends NetworkConstraint>> constraints = new HashSet<Class<? extends NetworkConstraint>>();

	NetworkModule(Class<? extends NetworkConnector> connector,
			Class<? extends NetworkController> controller) {
		super();
		this.connector = connector;
		this.controller = controller;
	}

	final NetworkModule withConstraints(
			Set<Class<? extends NetworkConstraint>> constraints) {
		this.constraints.addAll(constraints);
		return this;
	}

	/**
	 * Module to bind a fully connected network, where every agent can message
	 * every other agent with no limitations.
	 * 
	 * @return {@link AbstractModule} binding network interfaces.
	 */
	public static NetworkModule fullyConnectedNetworkModule() {
		return new NetworkModule(BasicNetworkConnector.class,
				NetworkController.class);
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
	public static NetworkModule constrainedNetworkModule(
			final Set<Class<? extends NetworkConstraint>> constraints) {
		return new NetworkModule(BasicNetworkConnector.class,
				ConstrainedNetworkController.class)
				.withConstraints(constraints);
	}

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(NetworkConnector.class,
				this.connector).build(NetworkConnectorFactory.class));
		bind(NetworkChannel.class).to(this.controller).in(Singleton.class);
		install(new FactoryModuleBuilder().build(NetworkAddressFactory.class));

		if (this.constraints.size() > 0) {
			Multibinder<NetworkConstraint> constraintBinder = Multibinder
					.newSetBinder(binder(), NetworkConstraint.class);
			Multibinder<EnvironmentService> serviceBinder = Multibinder
					.newSetBinder(binder(), EnvironmentService.class);
			for (Class<? extends NetworkConstraint> c : constraints) {
				constraintBinder.addBinding().to(c);
				
				if(c.isAnnotationPresent(ServiceDependencies.class)) {
					for (Class<? extends EnvironmentService> dep : c
							.getAnnotation(ServiceDependencies.class).value()) {
						serviceBinder.addBinding().to(dep);
					}
				}
			}
			
		}
	}

	public NetworkModule withNodeDiscovery() {
		this.connector = NetworkConnectorWithNodeDiscovery.class;
		return this;
	}

}
