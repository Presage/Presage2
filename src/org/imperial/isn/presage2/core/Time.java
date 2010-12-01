/**
 * 
 */
package org.imperial.isn.presage2.core;

/**
 * This is a generic representation of a time within the simulation. Using
 * this representation allows more complex time structures to be used rather
 * than just discrete integer time.
 * 
 * @author Sam Macbeth
 *
 */
public interface Time {

	public String toString();
	
	public boolean equals(Time t);
	
	/**
	 * Increment this time to the next discrete time value.
	 */
	public void increment();
	
	public void setTime(Time t);
	
	/**
	 * Clone this time
	 * @return
	 */
	public Time clone();
	
	/**
	 * 
	 * @param t time to compare to.
	 * @return true if this > t, false otherwise
	 */
	public boolean greaterThan(Time t);
	
}
