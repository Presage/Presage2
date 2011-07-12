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
import uk.ac.imperial.presage2.core.environment.ServiceDependencies;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.messaging.Input;

@ServiceDependencies({ LocationService.class })
public class MoveHandler implements ActionHandler {

	private final Logger logger = Logger.getLogger(MoveHandler.class);
	
	final protected HasArea environment;
	final protected LocationService locationService;
	
	/**
	 * @param environment
	 * @param serviceProvider
	 * @throws UnavailableServiceException 
	 */
	@Inject
	public MoveHandler(HasArea environment,
			EnvironmentServiceProvider serviceProvider) throws UnavailableServiceException {
		super();
		this.environment = environment;
		this.locationService = serviceProvider.getEnvironmentService(LocationService.class);
	}

	@Override
	public boolean canHandle(Action action) {
		return action instanceof Move;
	}

	@Override
	public Input handle(Action action, UUID actor)
			throws ActionHandlingException {
		if(action instanceof Move) {
			if(logger.isDebugEnabled())
				logger.debug("Handling move "+action+" from "+actor);
			final Move m = (Move) action;
			Location loc = null;
			try {
				loc = locationService.getAgentLocation(actor);
			} catch (CannotSeeAgent e) {
				throw new ActionHandlingException(e);
			}
			final Location target = Location.add(loc, m);
			if(target.in(environment.getArea())) {
				this.locationService.setAgentLocation(actor, target);
			} else {
				throw new ActionHandlingException("Cannot handle move to location outside of environment area.");
			}
			return null;
		}
		throw new ActionHandlingException("MoveHandler was asked to handle non Move action!");
	}

}
