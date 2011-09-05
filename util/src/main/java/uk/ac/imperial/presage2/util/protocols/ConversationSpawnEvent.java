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
package uk.ac.imperial.presage2.util.protocols;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.network.NetworkAddress;

/**
 * Event to spawn a new conversation and initialise it with recipients.
 * 
 * @author Sam Macbeth
 * 
 */
public class ConversationSpawnEvent {

	private final Set<NetworkAddress> targets = new HashSet<NetworkAddress>();

	ConversationSpawnEvent() {
		super();
	}

	ConversationSpawnEvent(NetworkAddress with) {
		super();
		targets.add(with);
	}

	ConversationSpawnEvent(Set<NetworkAddress> with) {
		super();
		targets.addAll(with);
	}

	/**
	 * Gets the {@link NetworkAddress}es of the agents this conversation should
	 * talk to.
	 * 
	 * @return
	 */
	public Set<NetworkAddress> getTargets() {
		return targets;
	}

}
