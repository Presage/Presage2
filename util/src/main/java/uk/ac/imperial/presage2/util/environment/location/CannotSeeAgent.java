package uk.ac.imperial.presage2.util.environment.location;

import java.util.UUID;

/**
 * 
 * Exception thrown when an agent requests directly for the {@link Location} of
 * another but the request agent is further away then the maximum distance the
 * former can perceive (as defined by {@link HasPerceptionRange}).
 *
 * @author Sam Macbeth
 *
 */
class CannotSeeAgent extends Exception {

	private static final long serialVersionUID = -3653607438569242041L;
	UUID me;
	UUID them;

	CannotSeeAgent(UUID me, UUID them) {
		this.me = me;
		this.them = them;
	}

	@Override
	public String getLocalizedMessage() {
		return "Agent "+ me +" cannot see "+ them +"";
	}

}
