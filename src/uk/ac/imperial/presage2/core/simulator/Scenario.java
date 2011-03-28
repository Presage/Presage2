/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

/**
 * @author Sam Macbeth
 *
 */
@PersistenceCapable
public abstract class Scenario {
	
	@PrimaryKey
	@Persistent
	protected int id;

	public abstract Set<Participant> getParticipants();
	
	public abstract Set<TimeDriven> getTimeDriven();
	
	public abstract Set<Plugin> getPlugins();
	
}
