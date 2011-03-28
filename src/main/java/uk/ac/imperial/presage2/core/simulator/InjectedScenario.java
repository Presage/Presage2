/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

/**
 * 
 * Instance of a Scenario where elements are injected via
 * Guice.
 * 
 * @author Sam Macbeth
 *
 */
public class InjectedScenario extends Scenario {
	
	final private Set<Participant> participants;
	
	final private Set<Plugin> plugins;
	
	final private Set<TimeDriven> timedriven;
	
	/**
	 * @param participants
	 * @param plugins
	 * @param timedriven
	 */
	@Inject
	protected InjectedScenario(Set<Participant> participants, Set<Plugin> plugins,
			Set<TimeDriven> timedriven) {
		super();
		this.id = 1;
		this.participants = participants;
		this.plugins = plugins;
		this.timedriven = timedriven;
	}

	@Override
	public Set<Participant> getParticipants() {
		return participants;
	}

	@Override
	public Set<TimeDriven> getTimeDriven() {
		// TODO Auto-generated method stub
		return timedriven;
	}

	@Override
	public Set<Plugin> getPlugins() {
		// TODO Auto-generated method stub
		return plugins;
	}

}
