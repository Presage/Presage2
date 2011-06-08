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
package uk.ac.imperial.presage2.util.environment.location;

/**
 * @author Sam Macbeth
 *
 */
public abstract class Location {

	/**
	 * 
	 * @return String representation of a Location
	 */
	public abstract String toString();

	/**
	 * Test whether the given location is equal to this one.
	 * @param l Location to compare to
	 * @return true iff this Location represents the same Location as l
	 */
	public abstract boolean equals(Location l);

	/**
	 * Get the distance between this Location and the location l
	 * @param l
	 * @return
	 */
	public abstract double distanceTo(Location l);

}
