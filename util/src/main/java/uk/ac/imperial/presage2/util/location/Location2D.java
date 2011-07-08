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

/**
 * A {@link Location} which is represented by two numerical coordinates.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class Location2D<T extends Number> extends Location {

	final protected T x;

	final protected T y;

	/**
	 * @param x
	 * @param y
	 */
	public Location2D(T x, T y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public Location getLocation() {
		return this;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	@Override
	public boolean equals(Location l) {
		if (l instanceof Location2D) {
			return this.equals((Location2D<?>) l);
		} else {
			return false;
		}
	}

	public boolean equals(Location2D<?> l) {
		try {
			return (this.x.equals(l.x) && this.y.equals(l.y));
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public double distanceTo(Location l) {
		if (l instanceof Location2D) {
			return this.distanceTo((Location2D<?>) l);
		} else
			throw new UnsupportedOperationException(
					"Distance between Locations "
							+ this.getClass().getSimpleName() + " and "
							+ l.getClass().getSimpleName());
	}

	public double distanceTo(Location2D<?> l) {
		if (l.x instanceof Integer) {
			final int dx = (int) Math.abs(l.x.doubleValue()
					- this.x.doubleValue());
			final int dy = (int) Math.abs(l.y.doubleValue()
					- this.y.doubleValue());
			return Math.sqrt(dx * dx + dy * dy);
		} else if (l.x instanceof Double) {
			final double dx = Math
					.abs(l.x.doubleValue() - this.x.doubleValue());
			final double dy = Math
					.abs(l.y.doubleValue() - this.y.doubleValue());
			return Math.sqrt(dx * dx + dy * dy);
		} else {
			throw new UnsupportedOperationException(
					"Distance between Locations "
							+ this.getClass().getSimpleName() + "<"
							+ this.x.getClass().getSimpleName() + "> and "
							+ l.getClass().getSimpleName() + "<"
							+ this.x.getClass().getSimpleName() + ">");
		}
	}

	public static Location add(Location2D<?> loc, Move2D<?> m) {
		final double x = loc.x.doubleValue() + m.x.doubleValue();
		final double y = loc.y.doubleValue() + m.y.doubleValue();
		if (loc.x instanceof Integer) {
			// preserve original location type.
			return new Discrete2DLocation((int) x, (int) y);
		} else {
			return new Continuous2DLocation(x, y);
		}
	}

	@Override
	public Move getMoveTo(Location l) {
		if (l instanceof Location2D<?>) {
			Location2D<?> l2 = (Location2D<?>) l;
			final double dx = l2.x.doubleValue() - this.x.doubleValue();
			final double dy = l2.y.doubleValue() - this.y.doubleValue();
			if (Math.floor(dx) == dx && Math.floor(dy) == dy) {
				return new Move2D<Integer>((int) dx, (int) dy);
			} else {
				return new Move2D<Double>(dx, dy);
			}
		}
		throw new UnsupportedOperationException(
				"Cannot move between dimensions!");
	}

	@Override
	public Move getMoveTo(Location l, double speed) {
		Move move = this.getMoveTo(l);
		if (move.getMagnitude() > speed) {
			final Move2D<?> m = (Move2D<?>) move;
			final double angle = m.getAngle();
			return new Move2D<Double>(Math.cos(angle) * speed, Math.sin(angle)
					* speed);
		} else
			return move;
	}

}
