/**
 * 
 */
package uk.ac.imperial.presage2.core.plugin;

import uk.ac.imperial.presage2.core.TimeDriven;

/**
 * @author Sam Macbeth
 *
 */
public interface Plugin extends TimeDriven {

	public void initialise();
	
	public void execute();
	
	public void onSimulationComplete();
	
}
