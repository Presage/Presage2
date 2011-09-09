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

import static org.junit.Assert.*;

import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.ParticipantSharedState;
import uk.ac.imperial.presage2.core.environment.SharedStateAccessException;
import uk.ac.imperial.presage2.core.util.random.Random;

public class TestLocationService {

	@Test
	public void testGetAgentLocation() throws CannotSeeAgent {
		final Mockery context = new Mockery();
		final EnvironmentSharedStateAccess mockEnv = context.mock(EnvironmentSharedStateAccess.class);
		final EnvironmentServiceProvider mockServiceProvider = context.mock(EnvironmentServiceProvider.class);
		
		final UUID validID = Random.randomUUID();
		final Location loc = new Location(0, 0);
		final UUID invalidID = Random.randomUUID();
		final LocationService serviceUnderTest = new LocationService(mockEnv, mockServiceProvider);
		
		context.checking(new Expectations() {{
			allowing(mockEnv).get("util.location", validID); will(returnValue(new ParticipantSharedState<Location>("util.location", loc, validID)));
			allowing(mockEnv).get("util.location", invalidID); will(throwException(new SharedStateAccessException()));
		}});
		
		assertSame(loc, serviceUnderTest.getAgentLocation(validID));
		
		try {
			serviceUnderTest.getAgentLocation(invalidID);
			fail();
		} catch (SharedStateAccessException e) {}
		
		context.assertIsSatisfied();
		
	}
	
}
