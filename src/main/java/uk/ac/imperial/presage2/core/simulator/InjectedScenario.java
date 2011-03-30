/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.Inject;

/**
 * 
 * <p>Instance of a {@link Scenario} where elements are injected via
 * Guice using a {@link ScenarioModule}.</p>
 * 
 * 
 * 
 * @author Sam Macbeth
 *
 */
public class InjectedScenario implements Scenario {
	
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
