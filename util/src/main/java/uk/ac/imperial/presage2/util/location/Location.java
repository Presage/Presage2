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

import uk.ac.imperial.presage2.util.location.area.Area;

/**
 * This represents a location in the environment space as defined by a 3D
 * vector. We the apache commons {@link Vector3D} class for this vector.
 * 
 * @author Sam Macbeth
 * 
 */
public class Location extends Vector3D implements HasLocation, Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a 2D location with the given x and y coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	public Location(double x, double y) {
		super(x, y, 0);
	}

	/**
	 * Create a 3D location with the given x, y and z coordinates.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Location(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Create a location from an existing {@link Vector3D}.
	 * 
	 * @param v
	 */
	public Location(Vector3D v) {
		super(v.getX(), v.getY(), v.getZ());
	}

	@Override
	protected Location clone() throws CloneNotSupportedException {
		return new Location(getX(), getY(), getZ());
	}

	/**
	 * Get the distance between this Location and the location l
	 * 
	 * @param l
	 * @return
	 */
	public double distanceTo(Location l) {
		return Vector3D.distance(this, l);
	}

	/**
	 * Returns the result of {@link Area#contains(Location)} for a and this.
	 * Allows more intuitive syntax when checking that a {@link Location} is in
	 * an {@link Area}.
	 * 
	 * @param a
	 * @return
	 */
	public boolean in(Area a) {
		return a.contains(this);
	}

	/**
	 * Not available in Immutable Location class.
	 */
	@Override
	public void setLocation(Location l) {
		throw new UnsupportedOperationException(
				"HasLocation#setLocation() not available in this context.");
	}

	public Vector3D getVector() {
		return this;
	}

	/**
	 * Get the direction vector between two point vectors.
	 * 
	 * @param l
	 * @return {@link Move}
	 */
	public Vector3D getVectorTo(Location l) {
		return l.subtract(this);
	}

	@Override
	public Location getLocation() {
		return this;
	}

	/**
	 * Get the {@link Move} from this Location which will result in the location
	 * <code>l</code>.
	 * 
	 * @param l
	 * @return {@link Move} m such that <code>this.add(m)</code> will return a
	 *         vector <code>v.equals(l)</code>
	 */
	public Move getMoveTo(Location l) {
		return new Move(getVectorTo(l));
	}

	/**
	 * Get the {@link Move} from this Location towards a location <code>l</code>
	 * with a magnitude less than or equal to <code>speed</code>.
	 * 
	 * @param l
	 * @param speed
	 * @return
	 */
	public Move getMoveTo(Location l, double speed) {
		Vector3D v = getVectorTo(l);
		if (v.getNorm() > speed)
			return new Move(v.normalize().scalarMultiply(speed));
		else
			return new Move(v);
	}

}
