/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import uk.ac.imperial.presage2.core.environment.SharedStateStorage;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SimulatorTest {

	Random rnd = new Random();
	final Mockery context = new Mockery();
	final SharedStateStorage ss = context.mock(SharedStateStorage.class);
	int time;
	Set<TestAgent> testedObjects;

	class TestSimulation extends RunnableSimulation {
		@Parameter
		public int param1;
		@Parameter(optional = true)
		public int paramOptional;

		@Override
		public void initialiseScenario(Scenario scenario) {
			testedObjects = new HashSet<TestAgent>();
			for (int i = 0; i < rnd.nextInt(10); i++) {
				TestAgent a = new TestAgent(time);
				scenario.addAgent(a);
				testedObjects.add(a);
			}
			TestAgent a = new TestAgent(time);
			scenario.addObject(a);
			testedObjects.add(a);
			scenario.addObject(new Condition(time));
			scenario.addClass(TestAgent.class);
			addModule(new AbstractModule() {
				@Override
				protected void configure() {
					bind(SharedStateStorage.class).toInstance(ss);
				}
			});
		}
	}

	static class TestAgent {
		boolean initialised = false;
		boolean finalised = false;
		int nextT = 0;
		int expectedT;

		@Inject
		public TestAgent(@Named("params.finishTime") int time) {
			this.expectedT = time;
		}

		@Initialisor
		public void init() {
			if (initialised)
				fail();
			initialised = true;
		}

		@Step
		public void step(int t) {
			if (!initialised)
				fail();
			assertEquals(nextT++, t);
		}

		@Step
		void nonPublicStep() {
			fail("Non public step function doesn't get scheduled");
		}

		@Finalisor
		public void finalise() {
			finalised = true;
			assertEquals(expectedT, nextT);
		}
	}

	static class Condition {
		int nextT = 0;
		int expectedT;

		public Condition(int time) {
			this.expectedT = time;
		}

		@FinishCondition
		public boolean condition(int t) {
			assertEquals(nextT++, t);
			return false;
		}
	}

	@Test
	public void testParameterLoading() throws IllegalArgumentException,
			IllegalAccessException, UndefinedParameterException {
		TestSimulation sim = new TestSimulation();
		Map<String, String> params = new HashMap<String, String>();
		final int p1 = rnd.nextInt();
		final int po = rnd.nextInt();
		final int time = rnd.nextInt();

		params.put("param1", Integer.toString(p1));
		params.put("paramOptional", Integer.toString(po));
		params.put("finishTime", Integer.toString(time));

		sim.loadParameters(params);

		assertEquals(p1, sim.param1);
		assertEquals(po, sim.paramOptional);
		assertEquals(time, sim.finishTime);
	}

	@Test
	public void testParameterOptional() throws IllegalArgumentException,
			IllegalAccessException, UndefinedParameterException {
		TestSimulation sim = new TestSimulation();
		Map<String, String> params = new HashMap<String, String>();
		final int p1 = rnd.nextInt();
		final int po = rnd.nextInt();
		final int time = rnd.nextInt();
		sim.paramOptional = po;

		params.put("param1", Integer.toString(p1));
		params.put("finishTime", Integer.toString(time));

		sim.loadParameters(params);

		assertEquals(p1, sim.param1);
		assertEquals(po, sim.paramOptional);
		assertEquals(time, sim.finishTime);
	}

	@Test
	public void testParameterMissing() throws IllegalArgumentException,
			IllegalAccessException, UndefinedParameterException {
		TestSimulation sim = new TestSimulation();
		Map<String, String> params = new HashMap<String, String>();
		final int time = rnd.nextInt();

		params.put("finishTime", Integer.toString(time));

		try {
			sim.loadParameters(params);
			fail();
		} catch (UndefinedParameterException e) {
		}
	}

	@Test
	public void testExtraParameter() throws IllegalArgumentException,
			IllegalAccessException, UndefinedParameterException {
		TestSimulation sim = new TestSimulation();
		Map<String, String> params = new HashMap<String, String>();
		final int time = rnd.nextInt();
		final int p1 = rnd.nextInt();

		params.put("param1", Integer.toString(p1));
		params.put("finishTime", Integer.toString(time));
		params.put("anotherparameter", "value");

		try {
			sim.loadParameters(params);
			fail();
		} catch (UndefinedParameterException e) {
		}
	}

	@Test
	public void testSimulationRun() throws IllegalArgumentException,
			IllegalAccessException, UndefinedParameterException {
		TestSimulation sim = new TestSimulation();
		Map<String, String> params = new HashMap<String, String>();
		final int p1 = rnd.nextInt();
		final int po = rnd.nextInt();
		time = rnd.nextInt(100);

		params.put("param1", Integer.toString(p1));
		params.put("paramOptional", Integer.toString(po));
		params.put("finishTime", Integer.toString(time));

		sim.loadParameters(params);

		context.checking(new Expectations() {
			{
				oneOf(ss).incrementTime();
			}
		});

		sim.initialise();
		context.assertIsSatisfied();
		for (TestAgent a : testedObjects) {
			assertTrue(a.initialised);
			assertFalse(a.finalised);
			assertEquals(0, a.nextT);
		}

		context.checking(new Expectations() {
			{
				exactly(time + 1).of(ss).incrementTime();
			}
		});
		sim.step();
		context.assertIsSatisfied();
		for (TestAgent a : testedObjects) {
			assertTrue(a.initialised);
			assertFalse(a.finalised);
			assertEquals(time + 1, a.nextT);
		}

		sim.finish();
		for (TestAgent a : testedObjects) {
			assertTrue(a.initialised);
			assertTrue(a.finalised);
			assertEquals(time + 1, a.nextT);
		}
	}

}
