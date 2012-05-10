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
package uk.ac.imperial.presage2.util.environment;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.StateTransformer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * This service provides access to the UUIDs of all the participants in the
 * simulation. This is a low level service to allow other services to do
 * searches on participants' shared states.
 * </p>
 * 
 * <p>
 * This service acts as a global environment services, and requires it's data to
 * be initialised through {@link #initialise(Map)} and
 * {@link #registerParticipant(EnvironmentRegistrationRequest, Map)} in order to
 * work correctly
 * </p>
 * 
 * <h3>Usage</h3>
 * 
 * <p>
 * If you're extending {@link AbstractEnvironment} this service is already
 * initialised in
 * {@link AbstractEnvironment#initialiseGlobalEnvironmentServices()}.
 * </p>
 * 
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class EnvironmentMembersService extends EnvironmentService {

	/**
	 * @param sharedState
	 */
	@Inject
	public EnvironmentMembersService(EnvironmentSharedStateAccess sharedState) {
		super(sharedState);
		this.sharedState.createGlobal("participants", new HashSet<UUID>());
	}

	/**
	 * Get a {@link Set} of the {@link UUID}s of the participants in the
	 * environment.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<UUID> getParticipants() {
		try {
			return Collections.unmodifiableSet((HashSet<UUID>) sharedState
					.getGlobal("participants"));
		} catch (ClassCastException e) {
			throw e;
		}
	}

	@Override
	public void registerParticipant(final EnvironmentRegistrationRequest req) {
		this.sharedState.changeGlobal("participants", new StateTransformer() {
			@SuppressWarnings("unchecked")
			@Override
			public Serializable transform(Serializable state) {
				HashSet<UUID> ids = (HashSet<UUID>) state;
				ids.add(req.getParticipantID());
				return ids;
			}
		});
	}

}
