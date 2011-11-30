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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.util.random.Random;
import uk.ac.imperial.presage2.util.location.area.Area;
import uk.ac.imperial.presage2.util.location.area.AreaService;
import uk.ac.imperial.presage2.util.location.area.HasArea;

public class TestLocationService {

	@Test
	public void testGetAgentLocation() throws CannotSeeAgent, UnavailableServiceException {
		final Mockery context = new Mockery();
		final EnvironmentSharedStateAccess mockEnv = context
				.mock(EnvironmentSharedStateAccess.class);
		final EnvironmentServiceProvider mockServiceProvider = context
				.mock(EnvironmentServiceProvider.class);
		final HasArea area = context.mock(HasArea.class);
		final Area a = new Area(1, 1, 1);

		context.checking(new Expectations() {
			{
				allowing(area).getArea();
				will(returnValue(a));
			}
		});

		final AreaService areaService = new AreaService(mockEnv, area);

		final UUID validID = Random.randomUUID();
		final Location loc = new Location(0, 0);
		final UUID invalidID = Random.randomUUID();

		context.checking(new Expectations() {
			{
				allowing(mockEnv).get("util.location", validID);
				will(returnValue(loc));
				allowing(mockEnv).get("util.location", invalidID);
				will(returnValue(null));
				allowing(mockServiceProvider).getEnvironmentService(AreaService.class);
				will(returnValue(areaService));
			}
		});

		final LocationService serviceUnderTest = new LocationService(mockEnv, mockServiceProvider);

		assertSame(loc, serviceUnderTest.getAgentLocation(validID));

		assertNull(serviceUnderTest.getAgentLocation(invalidID));

		context.assertIsSatisfied();

	}

}
