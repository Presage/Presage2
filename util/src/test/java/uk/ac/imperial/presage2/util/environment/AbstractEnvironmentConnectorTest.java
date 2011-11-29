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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnector;
import uk.ac.imperial.presage2.core.environment.EnvironmentConnectorTest;
import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.SharedStateStorage;
import uk.ac.imperial.presage2.core.participant.Participant;

public class AbstractEnvironmentConnectorTest extends EnvironmentConnectorTest {

	SharedStateStorage mockStorage;

	class TestAbstractEnvironment extends AbstractEnvironment {

		public TestAbstractEnvironment(SharedStateStorage sharedState) {
			super(sharedState);
		}

		@Override
		protected Set<ActionHandler> initialiseActionHandlers() {
			Set<ActionHandler> handlers = new HashSet<ActionHandler>();
			handlers.add(aHandler);
			return handlers;
		}

		@Override
		protected Set<EnvironmentService> generateServices(EnvironmentRegistrationRequest request) {
			return new HashSet<EnvironmentService>();
		}
	}

	@Override
	public EnvironmentConnector getEnvironmentConnector() {
		mockStorage = new MappedSharedState();
		return new TestAbstractEnvironment(mockStorage);
	}

	@Override
	public EnvironmentRegistrationRequest getRegistrationRequest(UUID id, Participant p) {
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
