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

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.*;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.participant.Participant;

public class AbstractEnvironmentConnectorTest extends EnvironmentConnectorTest {
	
	@Override
	public EnvironmentConnector getEnvironmentConnector() {
		return new AbstractEnvironment() {

			@Override
			public <T extends EnvironmentService> T getEnvironmentService(
					Class<T> type) throws UnavailableServiceException {
				return null;
			}

			@Override
			protected Set<ActionHandler> initialiseActionHandlers() {
				Set<ActionHandler> handlers = new HashSet<ActionHandler>();
				handlers.add(aHandler);
				return handlers;
			}

			@Override
			protected Set<EnvironmentService> generateServices(
					EnvironmentRegistrationRequest request) {
				return new HashSet<EnvironmentService>();
			}
			
		};
	}

	@Override
	public EnvironmentRegistrationRequest getRegistrationRequest(UUID id,
			Participant p) {
		return new EnvironmentRegistrationRequest(id, p);
	}

	@Override
	public Action getValidAction() {
		return action;
	}

	@Override
	public Set<Class<? extends EnvironmentService>> getExpectedServices() {
		return new HashSet<Class<? extends EnvironmentService>>();
	}

}
