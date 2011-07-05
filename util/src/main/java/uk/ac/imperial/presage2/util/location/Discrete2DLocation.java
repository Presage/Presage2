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
 * A 2D Location consisting of integer coordinates.
 * 
 * @author Sam Macbeth
 *
 */
public class Discrete2DLocation extends Location2D<Integer> {

	public Discrete2DLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Location add(Move m) {
		if(m instanceof Move2D) {
			if(((Move2D<?>) m).x instanceof Integer) {
				@SuppressWarnings("unchecked")
				final Move2D<Integer> m2 = (Move2D<Integer>) m;
				this.x += m2.x;
				this.y += m2.y;
			} else if(((Move2D<?>) m).x instanceof Double) {
				@SuppressWarnings("unchecked")
				final Move2D<Double> m2 = (Move2D<Double>) m;
				this.x = (int) Math.round(this.x.doubleValue() + m2.x);
				this.y = (int) Math.round(this.y.doubleValue() + m2.y);
			} else {
				throw new UnsupportedOperationException("Cannot add "+ m.getClass().getSimpleName() +" to "+ this.getClass().getSimpleName());
			}
			return this;
		} else {
			throw new UnsupportedOperationException("Cannot add "+ m.getClass().getSimpleName() +" to "+ this.getClass().getSimpleName());
		}
	}
	
}
