/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

/**
 * @author Sam Macbeth
 *
 */
public interface Scenario {

	public Set<Participant> getParticipants();
	
	public Set<TimeDriven> getTimeDriven();
	
	public Set<Plugin> getPlugins();
	
}
