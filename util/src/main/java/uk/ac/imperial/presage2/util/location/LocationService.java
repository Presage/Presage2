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

import java.util.Map;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.ParticipantStateTransformer;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.SharedState;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.util.location.area.AreaService;

import com.google.inject.Inject;

/**
 * An {@link EnvironmentService} to provide information on the locations of
 * agents.
 * 
 * <h3>Usage</h3>
 * 
 * <p>
 * Add as a global environment service in the environment
 * <p>
 * 
 * @author Sam Macbeth
 * 
 */
@ServiceDependencies({ AreaService.class })
public class LocationService extends EnvironmentService {

	EnvironmentServiceProvider serviceProvider;
	AreaService areaService;

	@Inject
	public LocationService(EnvironmentSharedStateAccess sharedState,
			EnvironmentServiceProvider serviceProvider) {
		super(sharedState);
		this.serviceProvider = serviceProvider;
	}

	@Override
	public void initialise(Map<String, SharedState<?>> globalSharedState) {
		super.initialise(globalSharedState);
		try {
			areaService = serviceProvider.getEnvironmentService(AreaService.class);
		} catch (UnavailableServiceException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * If any participant's location shared state is a 'Cell', we insert it into
	 * the global shared state via {@link AreaService}.
	 */
	@Override
	public void registerParticipant(EnvironmentRegistrationRequest req,
			Map<String, SharedState<?>> globalSharedState) {
		super.registerParticipant(req, globalSharedState);
		// add any cell locations to the cell map.
		for (ParticipantSharedState<?> s : req.getSharedState()) {
			if (s.getType().equals("util.location")
					&& ((HasLocation) s.getValue()).getLocation() instanceof Cell) {
				Cell c = (Cell) ((HasLocation) s.getValue()).getLocation();
				areaService.getCell((int) c.getX(), (int) c.getY(), (int) c.getZ()).add(
						req.getParticipantID());
			}
		}
	}

	/**
	 * Get the location of a given agent specified by it's participant UUID.
	 * 
	 * @param participantID
	 *            {@link UUID} of participant to look up
	 * @return {@link Location} of participants
	 */
	public Location getAgentLocation(UUID participantID) {
		return ((HasLocation) this.sharedState.get("util.location", participantID).getValue())
				.getLocation();
	}

	/**
	 * Update this agent's location to l.
	 * 
	 * If l is a {@link Cell} we update the information in the
	 * {@link AreaService}.
	 * 
	 * @param participantID
	 * @param l
	 */
	public void setAgentLocation(final UUID participantID, final Location l) {
		this.sharedState.change("util.location", participantID, new ParticipantStateTransformer() {
			@Override
			public void transform(ParticipantSharedState<?> state) {
				if (state.getValue() instanceof HasLocation) {
					HasLocation loc = (HasLocation) state.getValue();
					loc.setLocation(l);
				}
			}
		});
		if (l instanceof Cell) {
			synchronized (areaService) {
				// remove old location from map
				Location oldLoc = getAgentLocation(participantID);
				areaService.getCell((int) oldLoc.getX(), (int) oldLoc.getY(), (int) oldLoc.getZ())
						.remove(participantID);
				// add new loc to cell map
				areaService.getCell((int) l.getX(), (int) l.getY(), (int) l.getZ()).add(
						participantID);
			}
		}
	}

}
