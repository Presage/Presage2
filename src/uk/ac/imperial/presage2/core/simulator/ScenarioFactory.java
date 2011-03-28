/**
 * 
 */
package uk.ac.imperial.presage2.core.simulator;

import java.sql.Connection;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Sam Macbeth
 *
 */
@Singleton
public class ScenarioFactory {
	
	private final PersistenceManager pm;
	
	private final Logger logger = Logger.getLogger(ScenarioFactory.class);

	/**
	 * @param conn
	 */
	@Inject
	public ScenarioFactory(PersistenceManager pm) {
		super();
		this.pm = pm;
	}
	
	/**
	 * Get a {@link Scenario} from it's ID.
	 * @param scenarioID
	 * @param runID
	 * @return
	 */
	public Scenario get(int scenarioID) {
		List<Scenario> matches = (List<Scenario>) pm.newQuery(Scenario.class, "ID == "+scenarioID).execute();
		if(matches.size() == 1) {
			return matches.get(0);
		} else {
			logger.warn("Scenario "+ scenarioID +" not found!");
			return null; // TODO exception?
		}
	}
	
}
