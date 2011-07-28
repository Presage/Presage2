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

/**
 * @author Sam Macbeth
 * 
 */
public class Location extends Vector3D implements HasLocation, Cloneable {

	private static final long serialVersionUID = 1L;

	public Location(double x, double y) {
		super(x, y, 0);
	}

	public Location(double x, double y, double z) {
		super(x, y, z);
	}

	Location(Vector3D v) {
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
	 * Allows more intuitive syntax when changing that a {@link Location} is in
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

	public Move getMoveTo(Location l) {
		return new Move(getVectorTo(l));
	}

	public Move getMoveTo(Location l, double speed) {
		Vector3D v = getVectorTo(l);
		if (v.getNorm() > speed)
			return new Move(v.normalize().scalarMultiply(speed));
		else
			return new Move(v);
	}

}
