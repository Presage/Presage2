/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.core.simulator;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

public class ParameterTest {

	Random random;
	Map<String, String> providedParameters = new HashMap<String, String>();

	@Before
	public void clearParams() {
		providedParameters.clear();
		random = new Random();
	}

	/**
	 * Test a {@link RunnableSimulation} with no parameters needs
	 * <code>finishTime</code> parameter set.
	 * 
	 * @throws IllegalArgumentException
	 * @throws UndefinedParameterException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testZeroParameters() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {
			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));

		simUnderTest.setParameters(providedParameters);

		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
	}

	@Test
	public void testStringParameter() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1")
			public String param1;

			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));
		// set param1
		final String param1 = RandomStringUtils.random(random.nextInt(300));
		providedParameters.put("param1", param1);

		simUnderTest.setParameters(providedParameters);

		// assert time is set
		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
		// assert param1 is set
		assertEquals(param1, simUnderTest.getParameter("param1"));
	}

	@Test
	public void testIntegerParameter() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1")
			public int param1;

			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));
		// set param1
		final int param1 = random.nextInt();
		providedParameters.put("param1", Integer.toString(param1));

		simUnderTest.setParameters(providedParameters);

		// assert time is set
		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
		// assert param1 is set
		assertEquals(param1,
				Integer.parseInt(simUnderTest.getParameter("param1")));
	}

	@Test
	public void testDoubleParameter() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1")
			public double param1;

			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));
		// set param1
		final double param1 = random.nextInt() * random.nextDouble();
		providedParameters.put("param1", Double.toString(param1));

		simUnderTest.setParameters(providedParameters);

		// assert time is set
		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
		// assert param1 is set
		assertEquals(param1,
				Double.parseDouble(simUnderTest.getParameter("param1")), 0);
	}

	@Test
	public void testBooleanParameter() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1")
			public boolean param1;

			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));
		// set param1
		final boolean param1 = random.nextBoolean();
		providedParameters.put("param1", Boolean.toString(param1));

		simUnderTest.setParameters(providedParameters);

		// assert time is set
		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
		// assert param1 is set
		assertEquals(param1,
				Boolean.parseBoolean(simUnderTest.getParameter("param1")));
	}

	@Test
	public void testIgnoreNonPublicParameterField()
			throws IllegalArgumentException, UndefinedParameterException,
			IllegalAccessException, InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1")
			String param1;

			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));

		simUnderTest.setParameters(providedParameters);

		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
	}

	@Test
	public void testParameterNotSet() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1")
			public String param1;

			@Override
			public void load() {
			}
		};
		// finishTime is always required
		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));

		try {
			simUnderTest.setParameters(providedParameters);
			fail("Exception not thrown for undefined parameter");
		} catch (UndefinedParameterException e) {

		}
	}

	@Test
	public void testFinishTimeNotSet() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {
			@Override
			public void load() {
			}
		};
		try {
			simUnderTest.setParameters(providedParameters);
			fail("Exception not thrown for undefined parameter");
		} catch (UndefinedParameterException e) {

		}
	}

	@Test
	public void testOptionalParameter() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1", optional = true)
			public String param1 = "testValue";

			@Override
			public void load() {
			}
		};

		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));

		simUnderTest.setParameters(providedParameters);

		// assert time is set
		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
		// assert param1 is set
		assertEquals("testValue", simUnderTest.getParameter("param1"));
	}

	@Test
	public void testOptionalParameterIsSet() throws IllegalArgumentException,
			UndefinedParameterException, IllegalAccessException,
			InvocationTargetException {
		RunnableSimulation simUnderTest = new RunnableSimulation() {

			@Parameter(name = "param1", optional = true)
			public String param1 = "testValue";

			@Override
			public void load() {
			}
		};

		final int finishTime = random.nextInt(10000);
		providedParameters.put("finishTime", Integer.toString(finishTime));
		final String param1 = RandomStringUtils.random(random.nextInt(300));
		providedParameters.put("param1", param1);

		simUnderTest.setParameters(providedParameters);

		// assert time is set
		assertEquals(finishTime,
				Integer.parseInt(simUnderTest.getParameter("finishTime")));
		assertEquals(finishTime, simUnderTest.finishTime);
		// assert param1 is set
		assertEquals(param1, simUnderTest.getParameter("param1"));
	}

}
