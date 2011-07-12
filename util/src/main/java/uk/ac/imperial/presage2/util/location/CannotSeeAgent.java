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

package uk.ac.imperial.presage2.util.location;

import java.util.UUID;

import uk.ac.imperial.presage2.util.participant.HasPerceptionRange;

/**
 * 
 * Exception thrown when an agent requests directly for the {@link Location} of
 * another but the request agent is further away then the maximum distance the
 * former can perceive (as defined by {@link HasPerceptionRange}).
 *
 * @author Sam Macbeth
 *
 */
public class CannotSeeAgent extends RuntimeException {

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
