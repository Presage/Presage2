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
package uk.ac.imperial.presage2.core.db;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.core.db.persistent.TransientAgentState;
import uk.ac.imperial.presage2.core.util.random.Random;

public abstract class GenericStorageServiceTest {

	protected DatabaseService db;
	protected StorageService sto;

	@Before
	public void setUp() throws Exception {
		getDatabase();
		db.start();
	}

	@After
	public void tearDown() {
		db.stop();
	}

	/**
	 * Initialise <code>db</code> and <code>sto</code> variables so they can be
	 * used in a test.
	 */
	public abstract void getDatabase();

	@Test
	public void testStorage() {
		final String simName = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String simClass = RandomStringUtils.randomAlphanumeric(Random.randomInt(100));
		final String simState = RandomStringUtils.randomAlphanumeric(Random.randomInt(80));
		final int simFinish = Random.randomInt(100);
		final long timeBefore = System.currentTimeMillis();

		final PersistentSimulation sim = sto.createSimulation(simName, simClass, simState,
				simFinish);
		final long simID = sim.getID();

		// assert state of this simulation
		assertTrue(sim.getAgents().isEmpty());
		assertEquals(simClass, sim.getClassName());
		assertTrue(sim.getCreatedAt() >= timeBefore
				&& sim.getCreatedAt() <= System.currentTimeMillis());
		assertEquals(0, sim.getCurrentTime());
		assertEquals(simFinish, sim.getFinishTime());
		assertEquals(0, sim.getFinishedAt());
		assertEquals(simID, sim.getID());
		assertEquals(simName, sim.getName());
		assertTrue(sim.getParameters().isEmpty());
		assertNull(sim.getParentSimulation());
		assertEquals(0, sim.getStartedAt());
		assertEquals(simState, sim.getState());

		// check getSimulation() returns the current sim
		assertEquals(simID, sto.getSimulation().getID());

		// check getSimulationByID() returns current sim
		assertEquals(simID, sto.getSimulationById(simID).getID());

		// check getSimulationbyId() for non existing sim returns null
		assertNull(sto.getSimulationById(1875063));

		// setSimulation()
		sto.setSimulation(null);
		assertNull(sto.getSimulation());
		sto.setSimulation(sim);
		assertEquals(simID, sto.getSimulation().getID());

		// create agents
		final UUID aid = Random.randomUUID();
		final String agName = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final PersistentAgent agent = sto.createAgent(aid, agName);

		// assert state of agent
		assertEquals(aid, agent.getID());
		assertEquals(agName, agent.getName());
		// check simulation now has an agent
		assertFalse(sim.getAgents().isEmpty());
		assertTrue(sim.getAgents().size() == 1);

		// check transient agent state
		final int stateTime = Random.randomInt(100);
		final TransientAgentState state = sto.getAgentState(aid, stateTime);
		assertNotNull(state);
		assertEquals(aid, state.getAgent().getID());
		assertEquals(stateTime, state.getTime());

	}

	@Test
	public void testSimulation() {
		// create a simulation
		final String simName = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String simClass = RandomStringUtils.randomAlphanumeric(Random.randomInt(100));
		final String simState = RandomStringUtils.randomAlphanumeric(Random.randomInt(80));
		final int simFinish = Random.randomInt(100);

		final PersistentSimulation sim = sto.createSimulation(simName, simClass, simState,
				simFinish);
		final long simID = sim.getID();

		// test setters
		final int newTime = Random.randomInt(Integer.MAX_VALUE);
		sim.setCurrentTime(newTime);
		assertEquals(newTime, sim.getCurrentTime());
		final long newFinish = Random.getInstance().nextLong();
		sim.setFinishedAt(newFinish);
		assertEquals(newFinish, sim.getFinishedAt());
		final long newStart = Random.getInstance().nextLong();
		sim.setStartedAt(newStart);
		assertEquals(newStart, sim.getStartedAt());
		final String newState = RandomStringUtils.randomAlphanumeric(Random.randomInt(80));
		sim.setState(newState);
		assertEquals(newState, sim.getState());

		// get a new copy of sim from db to check values were persisted
		final PersistentSimulation sim2 = sto.getSimulationById(simID);
		assertEquals(newTime, sim2.getCurrentTime());
		assertEquals(newFinish, sim2.getFinishedAt());
		assertEquals(newStart, sim2.getStartedAt());
		assertEquals(newState, sim2.getState());

		// add parameter
		final String paramName1 = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String paramValue1 = RandomStringUtils.randomAlphanumeric(Random.randomInt(200));
		sim.addParameter(paramName1, paramValue1);
		Map<String, Object> params = sim.getParameters();
		assertTrue(params.size() == 1);
		assertTrue(params.containsKey(paramName1));
		assertEquals(paramValue1, params.get(paramName1).toString());

		final String paramName2 = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String paramValue2 = RandomStringUtils.randomAlphanumeric(Random.randomInt(200));
		sim.addParameter(paramName2, paramValue2);
		params = sim.getParameters();
		assertTrue(params.size() == 2);
		assertTrue(params.containsKey(paramName2));
		assertEquals(paramValue2, params.get(paramName2).toString());

		// test sim parent/children
		final PersistentSimulation sim3 = sto.createSimulation(simName, simClass, simState,
				simFinish);
		final PersistentSimulation sim4 = sto.createSimulation(simName, simClass, simState,
				simFinish);
		assertNull(sim3.getParentSimulation());
		assertTrue(sim.getChildren().size() == 0);
		sim3.setParentSimulation(sim);
		assertEquals(simID, sim3.getParentSimulation().getID());
		List<Long> children = sim.getChildren();
		assertTrue(children.size() == 1);
		assertEquals(sim3.getID(), children.get(0).longValue());

		assertNull(sim4.getParentSimulation());
		sim4.setParentSimulation(sim2); // same as sim
		assertEquals(simID, sim4.getParentSimulation().getID());
		children = sim.getChildren();
		assertTrue(children.size() == 2);
		assertTrue(children.contains(sim4.getID()));
		assertTrue(children.contains(sim3.getID()));
	}

	@Test
	public void testEnvironment() {
		final String simName = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String simClass = RandomStringUtils.randomAlphanumeric(Random.randomInt(100));
		final String simState = RandomStringUtils.randomAlphanumeric(Random.randomInt(80));
		final int simFinish = Random.randomInt(100);

		final PersistentSimulation sim = sto.createSimulation(simName, simClass, simState,
				simFinish);

		PersistentEnvironment env = sim.getEnvironment();
		assertNotNull(env);

		final String paramName1 = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String paramValue1 = RandomStringUtils.randomAlphanumeric(Random.randomInt(200));

		assertNull(env.getProperty(paramName1));
		env.setProperty(paramName1, paramValue1);
		assertEquals(paramValue1, env.getProperty(paramName1));

		final int timestep = Random.randomInt(1000);
		final String paramValue2 = RandomStringUtils.randomAlphanumeric(Random.randomInt(200));
		assertNull(env.getProperty(paramName1, timestep));
		env.setProperty(paramName1, timestep, paramValue2);
		assertEquals(paramValue2, env.getProperty(paramName1, timestep));
		assertNull(env.getProperty(paramName1, timestep + 1));
	}

	@Test
	public void testAgent() {
		PersistentAgent agent;
		try {
			agent = sto.createAgent(Random.randomUUID(), RandomStringUtils.randomAlphanumeric(10));
			fail();
		} catch (RuntimeException e) {
		}
		sto.createSimulation(RandomStringUtils.randomAlphanumeric(Random.randomInt(20)),
				RandomStringUtils.randomAlphanumeric(Random.randomInt(100)),
				RandomStringUtils.randomAlphanumeric(Random.randomInt(80)), Random.randomInt(100));

		agent = sto.createAgent(Random.randomUUID(), RandomStringUtils.randomAlphanumeric(10));

		// test agent properties
		assertNull(agent.getProperty(RandomStringUtils.randomAlphabetic(Random.randomInt(20))));

		final String propKey = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String propVal = RandomStringUtils.randomAlphanumeric(Random.randomInt(200));
		agent.setProperty(propKey, propVal);
		assertNull(agent.getProperty(RandomStringUtils.randomAlphabetic(Random.randomInt(20))));
		assertEquals(propVal, agent.getProperty(propKey).toString());

		// test agent state
		final TransientAgentState state = agent.getState(0);
		assertEquals(agent.getID(), state.getAgent().getID());
		assertEquals(0, state.getTime());

		assertNull(state.getProperty(RandomStringUtils.randomAlphabetic(Random.randomInt(20))));

		// set and get a property on the state
		final String spropKey = RandomStringUtils.randomAlphanumeric(Random.randomInt(20));
		final String spropVal = RandomStringUtils.randomAlphanumeric(Random.randomInt(200));
		state.setProperty(spropKey, spropVal);
		assertEquals(spropVal, state.getProperty(spropKey).toString());
		assertNull(state.getProperty(RandomStringUtils.randomAlphabetic(Random.randomInt(20))));

		// get same property from state for different time
		final TransientAgentState state2 = agent.getState(Random.randomInt(1000) + 1);
		assertNull(state2.getProperty(spropKey));

		// check our data has persisted
		final PersistentAgent ag = sto.getAgent(agent.getID());
		assertEquals(propVal, ag.getProperty(propKey).toString());
		assertEquals(spropVal, ag.getState(0).getProperty(spropKey).toString());
	}

}
