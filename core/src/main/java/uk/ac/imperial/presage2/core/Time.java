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

package uk.ac.imperial.presage2.core;

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
	
	/**
	 * Check if two times are equal.
	 * @param t
	 * @return true if both Times represent the same discrete simulation time.
	 */
	public boolean equals(Time t);
	
	/**
	 * Increment this time to the next discrete time value.
	 */
	public void increment();
	
	public void setTime(Time t);
	
	/**
	 * Clone this time
	 * @return clone of this time.
	 */
	public Time clone();
	
	/**
	 * 
	 * @param t time to compare to.
	 * @return true if this > t, false otherwise
	 */
	public boolean greaterThan(Time t);
	
}
