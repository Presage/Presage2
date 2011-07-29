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
package uk.ac.imperial.presage2.util.location;

import org.apache.commons.math.geometry.Vector3D;

import uk.ac.imperial.presage2.core.Action;

/**
 * An Action which represents a change in {@link Location}. Defined by a
 * {@link Vector3D}.
 * 
 * @author Sam Macbeth
 * 
 */
public class Move extends Vector3D implements Action {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a {@link Move} in 2 dimensions.
	 * 
	 * @param x
	 * @param y
	 */
	public Move(double x, double y) {
		super(x, y, 0);
	}

	/**
	 * Create a {@link Move} in 3 dimensions.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Move(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Create a move from an existing {@link Vector3D}.
	 * 
	 * @param v
	 */
	public Move(Vector3D v) {
		super(v.getX(), v.getY(), v.getZ());
	}

}
