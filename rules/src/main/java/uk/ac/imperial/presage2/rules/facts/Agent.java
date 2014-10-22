/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.rules.facts;

import java.util.UUID;

public class Agent {

	public UUID aid;

	public Agent() {
		super();
	}

	public Agent(UUID aid) {
		super();
		this.aid = aid;
	}

	@Override
	public int hashCode() {
		return aid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Agent other = (Agent) obj;
		if (aid == null) {
			if (other.aid != null) {
				return false;
			}
		} else if (!aid.equals(other.aid)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Agent [aid=" + aid + "]";
	}

	public UUID getAid() {
		return aid;
	}

	public void setAid(UUID aid) {
		this.aid = aid;
	}

}
