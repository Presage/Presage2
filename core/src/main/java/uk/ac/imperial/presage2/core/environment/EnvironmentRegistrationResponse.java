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

import java.util.Set;
import java.util.UUID;

/**
 * <p>The response to a {@link EnvironmentRegistrationRequest}. This will
 * provide an {@link EnvironmentRegistrationResponse#authKey} which allows the
 * participant to act via the {@link EnvironmentConnector}.</p>
 * 
 * <p>It will also contain a set of {@link EnvironmentService}s for access of 
 * the environment's shared state</p>
 * @author Sam Macbeth
 *
 */
public class EnvironmentRegistrationResponse {

	private UUID authKey;
	
	private Set<EnvironmentService> services;

	/**
	 * @param authKey
	 * @param services
	 */
	public EnvironmentRegistrationResponse(UUID authKey,
			Set<EnvironmentService> services) {
		super();
		this.authKey = authKey;
		this.services = services;
	}

	/**
	 * @return the authKey
	 */
	public UUID getAuthKey() {
		return authKey;
	}

	/**
	 * @return the services
	 */
	public Set<EnvironmentService> getServices() {
		return services;
	}
	
}
