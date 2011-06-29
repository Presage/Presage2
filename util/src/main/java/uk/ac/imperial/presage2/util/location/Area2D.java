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
 * @author Sam Macbeth
 *
 */
public class Area2D implements Area {

	private int x;
	
	private int y;
	
	/**
	 * @param x
	 * @param y
	 */
	public Area2D(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean contains(Location l) {
		if(l instanceof Location2D) {
			Location2D<?> l2 = (Location2D<?>) l;
			double x = (Double) l2.x;
			double y = (Double) l2.y;
			return x <= this.x && y <= this.y
					&& x >= 0 && y >= 0;
		} else 
			return false;
	}

}
