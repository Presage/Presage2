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
package uk.ac.imperial.presage2.rules.facts;

import java.io.Serializable;
import java.util.UUID;


public class AgentStateFact extends StateFact {

	UUID agentId;

	AgentStateFact(String identifier, Serializable value, UUID agentId) {
		super(identifier, value);
		this.agentId = agentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((agentId == null) ? 0 : agentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AgentStateFact other = (AgentStateFact) obj;
		if (agentId == null) {
			if (other.agentId != null) {
				return false;
			}
		} else if (!agentId.equals(other.agentId)) {
			return false;
		}
		return true;
	}

	public UUID getAgentId() {
		return agentId;
	}

	void setAgentId(UUID agentId) {
		this.agentId = agentId;
	}

}
