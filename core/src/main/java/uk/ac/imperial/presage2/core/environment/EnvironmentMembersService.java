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
package uk.ac.imperial.presage2.core.environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 * <p>This service provides access to the UUIDs of all the participants in the
 * simulation. This is a low level service to allow other services to do searches
 * on participants' shared states.</p>
 * 
 * 
 * @author Sam Macbeth
 *
 */
public class EnvironmentMembersService extends EnvironmentService {

	/**
	 * @param sharedState
	 */
	public EnvironmentMembersService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
	}

	/**
	 * Get a {@link Set} of the {@link UUID}s of the participants in the environment.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<UUID> getParticipants() {
		try {
			return Collections.unmodifiableSet((Set<UUID>) sharedState.getGlobal("participants").getValue());
		} catch(ClassCastException e) {
			throw e;
		}
	}

	@Override
	public void initialise(Map<String, SharedState<?>> globalSharedState) {
		// create the 'participants' attribute in global shared state.
		globalSharedState.put("participants", new SharedState<Set<UUID>>("participants", new HashSet<UUID>()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerParticipant(EnvironmentRegistrationRequest req,
			Map<String, SharedState<?>> globalSharedState) {
		// add entry in 'participants' global state attribute
		((Set<UUID>) globalSharedState.get("participants").getValue()).add(req.participantID);
	}

}
