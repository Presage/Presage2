/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.rules.facts.location;

import java.util.UUID;

import uk.ac.imperial.presage2.rules.facts.Agent;

public class Location {

	public Agent agent;
	public uk.ac.imperial.presage2.util.location.Location location;
	public long timestamp;

	public Location(Agent agent,
			uk.ac.imperial.presage2.util.location.Location location) {
		super();
		this.agent = agent;
		this.location = location;
	}

	public Location(UUID agent,
			uk.ac.imperial.presage2.util.location.Location location) {
		super();
		this.agent = new Agent(agent);
		this.location = location;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public uk.ac.imperial.presage2.util.location.Location getLocation() {
		return location;
	}

	public void setLocation(
			uk.ac.imperial.presage2.util.location.Location location) {
		this.location = location;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
