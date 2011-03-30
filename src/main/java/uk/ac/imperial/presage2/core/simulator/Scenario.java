/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

/**
 * 
 * <p>A Scenario describes the runtime components of a simulation which the
 * simulator must interact with. These comprise of the {@link Participant}s of the
 * system, the other {@link TimeDriven} elements, and the {@link Plugin}s. The Scenario
 * will likely have to construct other elements which the above require such as network
 * components and the environment.</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface Scenario {

	public Set<Participant> getParticipants();
	
	public Set<TimeDriven> getTimeDriven();
	
	public Set<Plugin> getPlugins();
	
}
