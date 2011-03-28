/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

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
	
	@Inject
	public Simulator(Scenario scenario) {
		this.scenario = scenario;
	}

	/**
	 * Initialise simulation components.
	 */
	public abstract void initialise();
	
	public abstract void run();
	
}
