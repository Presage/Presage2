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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
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
	public void testCreateSimulation() {
		final String simName = "Test";
		final String simClass = "Testclass";
		final String simState = "TEST";
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
		// assertNull(sim.getParentSimulation());
		assertEquals(0, sim.getStartedAt());
		assertEquals(simState, sim.getState());

	}

}
