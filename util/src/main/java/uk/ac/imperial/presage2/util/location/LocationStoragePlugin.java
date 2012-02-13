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

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.plugin.Plugin;
import uk.ac.imperial.presage2.util.environment.EnvironmentMembersService;

import com.google.inject.Inject;

public class LocationStoragePlugin implements Plugin {

	private final Logger logger = Logger.getLogger(LocationStoragePlugin.class);

	private StorageService storage;

	private final EnvironmentMembersService membersService;
	private final LocationService locService;

	private final Time time;

	public LocationStoragePlugin() {
		super();
		storage = null;
		locService = null;
		membersService = null;
		time = null;
		logger.info("I wasn't given a storage service, I won't do anything!");
	}

	@Inject
	public LocationStoragePlugin(EnvironmentServiceProvider serviceProvider,
			Time t) throws UnavailableServiceException {
		this.storage = null;
		this.membersService = serviceProvider
				.getEnvironmentService(EnvironmentMembersService.class);
		this.locService = serviceProvider
				.getEnvironmentService(LocationService.class);
		this.time = t;
	}

	@Inject(optional = true)
	public void setStorage(StorageService storage) {
		this.storage = storage;
	}

	@Override
	public void incrementTime() {
		if (this.storage != null) {
			for (UUID pid : this.membersService.getParticipants()) {
				Location l;
				try {
					l = this.locService.getAgentLocation(pid);
				} catch (Exception e) {
					logger.debug("Exception getting agent location.", e);
					continue;
				}
				TransientAgentState state = this.storage.getAgentState(pid,
						time.intValue());
				state.setProperty("x", Double.toString(l.getX()));
				state.setProperty("y", Double.toString(l.getY()));
				state.setProperty("z", Double.toString(l.getZ()));
			}
		}
		time.increment();
	}

	@Override
	public void initialise() {
	}

	@Override
	public void execute() {
	}

	@Override
	public void onSimulationComplete() {

	}
}
