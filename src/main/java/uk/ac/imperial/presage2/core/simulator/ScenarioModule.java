/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.participant.ParticipantFactory;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

/**
 * 
 * <p>The ScenarioModule is an {@link AbstractModule} which attempts to ensure
 * that sub classes bind sufficiently such that {@link InjectedScenario} can create
 * a full scenario.</p>
 * 
 * <p>Bindings are made to {@link Provider}s which the sub class must define, and are accessed via
 * the abstract getters. The following classes are bound:
 * 	<ul>
 * 		<li> {@link EnvironmentConnector}</li>
 * 		<li> {@link EnvironmentServiceProvider}</li>
 * 		<li> {@link EnvironmentSharedStateAccess}</li>
 * 	</ul>
 * </p>
 * 
 * @author Sam Macbeth
 *
 */
@PersistenceCapable
public abstract class ScenarioModule extends AbstractModule {
	
	/**
	 * 
	 */
	@Override
	abstract protected void configure();

}
