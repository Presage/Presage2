/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.sql.Connection;
import java.util.Set;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;

import uk.ac.imperial.presage2.core.TimeDriven;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.plugin.Plugin;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Sam Macbeth
 *
 */
public abstract class Simulator {

	private static Logger logger = Logger.getLogger(Simulator.class);
	/**
	 * The Scenario to simulate
	 */
	protected Scenario scenario;
	
	/**
	 * A database connection for the simulator to use.
	 */
	protected Connection db;
	
	@Inject
	public Simulator(Connection db) {
		this.db = db;
	}
	
	@Inject
	public Simulator(Connection db, Scenario scenario) {
		this.db = db;
		this.scenario = scenario;
	}

	/**
	 * Initialise simulation components.
	 */
	public abstract void initialise();
	
	public abstract void run();
	
}
