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

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.util.location.area.Area;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.util.location.area.EdgeException;
import uk.ac.imperial.presage2.util.location.area.HasArea;

@ServiceDependencies({ LocationService.class, AreaService.class })
public class MoveHandler implements ActionHandler {

	private final Logger logger = Logger.getLogger(MoveHandler.class);

	final protected HasArea environment;
	final protected EnvironmentSharedStateAccess sharedState;
	final protected EnvironmentServiceProvider serviceProvider;
	private LocationService locationService = null;
	private AreaService areaService = null;

	@Inject
	public MoveHandler(HasArea environment,
			EnvironmentServiceProvider serviceProvider,
			EnvironmentSharedStateAccess sharedState)
			throws UnavailableServiceException {
		super();
		this.environment = environment;
		this.serviceProvider = serviceProvider;

		this.sharedState = sharedState;
		this.areaService = serviceProvider
				.getEnvironmentService(AreaService.class);
	}

	@Override
	public boolean canHandle(Action action) {
		return action instanceof Move;
	}

	protected LocationService getLocationService() {
		if (locationService == null) {
			try {
				this.locationService = serviceProvider
						.getEnvironmentService(LocationService.class);
			} catch (UnavailableServiceException e) {
				logger.warn("Could not load location service", e);
			}
		}
		return locationService;
	}

	protected AreaService getAreaService() {
		if (areaService == null) {
			try {
				this.areaService = serviceProvider
						.getEnvironmentService(AreaService.class);
			} catch (UnavailableServiceException e) {
				logger.warn("Could not load area service", e);
			}
		}
		return areaService;
	}

	/**
	 * Processes a {@link Move} action.
	 * 
	 * In the case of a basic {@link Move} we do a vector addition of the
	 * agent's current location with the move to determine a resultant location.
	 * This is then passed to the {@link LocationService} to update the agent's
	 * location. If the resultant location is not in the simulation {@link Area}
	 * we ask the area for a valid version of the move and apply that instead.
	 * 
	 * In the case of a {@link CellMove} we simply check the destination cell,
	 * and if it's empty we set the new location through the
	 * {@link LocationService}. Otherwise we throw an
	 * {@link ActionHandlingException}.
	 */
	@Override
	public Input handle(Action action, UUID actor)
			throws ActionHandlingException {
		getLocationService();
		if (action instanceof CellMove) {
			getAreaService();
			final Move m = (CellMove) action;
			synchronized (areaService) {
				if (areaService.getCell((int) m.getX(), (int) m.getY(),
						(int) m.getZ()).size() == 0) {
					Location target = new Cell((int) m.getX(), (int) m.getY(),
							(int) m.getZ());
					if (!target.in(environment.getArea())) {
						try {
							Location loc = locationService
									.getAgentLocation(actor);
							final Move mNew = environment.getArea()
									.getValidMove(loc, m);
							target = new Location(loc.add(mNew));
						} catch (EdgeException e) {
							throw new ActionHandlingException(e);
						}
					}
					this.locationService.setAgentLocation(actor, target);
					return null;
				} else {
					throw new ActionHandlingException(
							"Target cell already occupied.");
				}
			}
		}
		if (action instanceof Move) {
			if (logger.isDebugEnabled())
				logger.debug("Handling move " + action + " from " + actor);
			final Move m = (Move) action;
			Location loc = null;
			try {
				loc = locationService.getAgentLocation(actor);
			} catch (CannotSeeAgent e) {
				throw new ActionHandlingException(e);
			}
			Location target = new Location(loc.add(m));
			if (!target.in(environment.getArea())) {
				try {
					final Move mNew = environment.getArea()
							.getValidMove(loc, m);
					target = new Location(loc.add(mNew));
				} catch (EdgeException e) {
					throw new ActionHandlingException(e);
				}
			}
			this.locationService.setAgentLocation(actor, target);
			return null;
		}
		throw new ActionHandlingException(
				"MoveHandler was asked to handle non Move action!");
	}

}
