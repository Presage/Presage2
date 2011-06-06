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

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.NetworkAddressFactory;
import uk.ac.imperial.presage2.core.network.NetworkConnector;
import uk.ac.imperial.presage2.core.network.NetworkConnectorFactory;
import uk.ac.imperial.presage2.core.network.NetworkController;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.AbstractModule;

/**
 * 
 * <p>A ScenarioModule provides Guice bindings which allow the simulation components to
 * be loaded by an {@link InjectedScenario}.</p>
 * 
 * <p>Depending on your simulation you will likely need to bind the following:
 * 	<ul>
 * 		<li> {@link Time}</li>
 * 		<li> {@link EnvironmentConnector}</li>
 * 		<li> {@link EnvironmentServiceProvider}</li>
 * 		<li> {@link EnvironmentSharedStateAccess}</li>
 * 		<li> {@link NetworkConnector}</li>
 * 		<li> {@link NetworkController}</li>
 * 		<li> {@link NetworkAddress}</li>
 * 		<li> {@link Participant}</li>
 * 		<li> {@link Plugin}s</li>
 * 		<li> {@link TimeDriven}s</li>
 *  </ul>
 * </p>
 * 
 * <p>
 * 	For alot of these bindings you can simply bind the interface to the implementation you would like to use, e.g.:
 *
 * 	<pre class="prettyprint">
 * bind(Time.class).to(IntegerTime.class);</pre>
 * 	
 * 	If you want an implementation to be a singleton use <code>.in(Singleton.class</code>:
 * 
 * 	<pre class="prettyprint">
 * bind(BasicEnvironment.class).in(Singleton.class); // Singleton without binding
 * bind(NetworkChannel.class).to(NetworkController.class).in(Singleton.class); // Singleton and binding combined</pre>
 * 
 *  In some cases you will want an object parameterised via some kind of factory. You can bind and inject the factory in these cases.
 *  See {@link NetworkConnectorFactory} and {@link NetworkAddressFactory} for examples:
 *  <pre class="prettyprint">
 * install(new FactoryModuleBuilder().implement(NetworkConnector.class, BasicNetworkConnector.class).build(NetworkConnectorFactory.class));
 * install(new FactoryModuleBuilder().implement(Participant.class, BasicAgent.class).build(ParticipantFactory.class));</pre>
 * </p>
 * @author Sam Macbeth
 *
 */
public abstract class ScenarioModule extends AbstractModule {
	
	@Override
	abstract protected void configure();

}
