package uk.ac.imperial.presage2.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
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
