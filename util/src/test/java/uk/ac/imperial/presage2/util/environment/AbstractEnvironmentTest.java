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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.imperial.presage2.core.environment.ActionHandler;
import uk.ac.imperial.presage2.core.environment.EnvironmentRegistrationRequest;
import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;

/**
 * @author Sam Macbeth
 *
 */
public class AbstractEnvironmentTest {

	//Mockery context = new Mockery();
	
	class TestAbstractEnvironment extends AbstractEnvironment {

		@Override
		protected Set<ActionHandler> initialiseActionHandlers() {
			return Collections.emptySet();
		}

		@Override
		protected Set<EnvironmentService> generateServices(
				EnvironmentRegistrationRequest request) {
			final Set<EnvironmentService> services = new HashSet<EnvironmentService>();
			services.add(new MockEnvironmentService(this));
			return services;
		}
		
	}
	
	class MockEnvironmentService extends EnvironmentService {
		MockEnvironmentService(
				EnvironmentSharedStateAccess sharedState) {
			super(sharedState);
		}
	}
	
	@Test
	public void testHasEnvironmentMembersService() throws UnavailableServiceException {
		final TestAbstractEnvironment envUnderTest = new TestAbstractEnvironment();
		final EnvironmentService envService = envUnderTest.getEnvironmentService(EnvironmentMembersService.class);
		assertNotNull(envService);
		assertTrue(envService instanceof EnvironmentMembersService);
	}
	
	@Test
	public void testGetEnvironmentServiceFailure() {
		final TestAbstractEnvironment envUnderTest = new TestAbstractEnvironment();
		try {
			final EnvironmentService envService = envUnderTest.getEnvironmentService(null);
			fail("No exception thrown by AbstractEnvironment.getEnvironmentService(null), NullPointerException expected.");
		} catch(NullPointerException e) {
			
		} catch (UnavailableServiceException e) {
			fail("UnavailableServiceException exception thrown by AbstractEnvironment.getEnvironmentService(null), NullPointerException expected.");
		}
		try {
			final EnvironmentService envService = envUnderTest.getEnvironmentService(MockEnvironmentService.class);
			fail("No exception thrown by AbstractEnvironment.getEnvironmentService(MockEnvironmentService.class), UnavailableServiceException expected.");
		} catch (UnavailableServiceException e) {}
	}
	
	
}
