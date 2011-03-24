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
	
	/**
	 * @deprecated Use {@link TimeDriven#incrementTime()} now.
	 */
	@Deprecated
	public void execute();
	
	public void onSimulationComplete();
	
}
