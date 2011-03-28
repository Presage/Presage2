/**
 * 
 */
package uk.ac.imperial.presage2.core.participant;

import java.util.UUID;

/**
 * @author Sam Macbeth
 *
 */
public interface ParticipantFactory {

	public Participant create(UUID id, String name);
	
}
