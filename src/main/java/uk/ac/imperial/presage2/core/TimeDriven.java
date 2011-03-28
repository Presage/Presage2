/**
 * 
 */
package uk.ac.imperial.presage2.core;

/**
 * <p>Interface for any element in the simulation which
 * wants to be informed of increments in the global clock.</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface TimeDriven {

	public void incrementTime();
	
}
