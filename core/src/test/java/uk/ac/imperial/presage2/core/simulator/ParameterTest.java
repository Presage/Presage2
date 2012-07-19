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

}
