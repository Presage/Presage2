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

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;

import com.google.inject.Inject;

public class LocationStoragePlugin {

	private final Logger logger = Logger.getLogger(LocationStoragePlugin.class);

	private PersistentSimulation storage;

	private final EnvironmentMembersService membersService;
	private final LocationService locService;

	public LocationStoragePlugin() {
		super();
		storage = null;
		locService = null;
		membersService = null;
		logger.info("I wasn't given a storage service, I won't do anything!");
	}

	@Inject
	public LocationStoragePlugin(EnvironmentServiceProvider serviceProvider) throws UnavailableServiceException {
		this.storage = null;
		this.membersService = serviceProvider
				.getEnvironmentService(EnvironmentMembersService.class);
		this.locService = serviceProvider
				.getEnvironmentService(LocationService.class);
	}

	@Inject(optional = true)
	public void setStorage(PersistentSimulation psim) {
		this.storage = psim;
	}

	@Step
	public void incrementTime(int t) {
		if (this.storage != null) {
			for (UUID pid : this.membersService.getParticipants()) {
				Location l;
				try {
					l = this.locService.getAgentLocation(pid);
				} catch (Exception e) {
					logger.debug("Exception getting agent location.", e);
					continue;
				}
				if (l == null)
					continue;

				this.storage.storeTuple("x", pid, t, l.getX());
				this.storage.storeTuple("y", pid, t, l.getY());
				this.storage.storeTuple("z", pid, t, l.getZ());
			}
		}
	}
}
