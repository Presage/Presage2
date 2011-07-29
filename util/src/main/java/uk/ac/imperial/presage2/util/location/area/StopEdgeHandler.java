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
package uk.ac.imperial.presage2.util.location.area;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;
import uk.ac.imperial.presage2.util.location.area.Area.Edge;

/**
 * An {@link EdgeHandler} which modifies {@link Move}s so the agent stops at the
 * extreme edge of the simulation {@link Area}.
 * 
 * @author Sam Macbeth
 * 
 */
public class StopEdgeHandler implements EdgeHandler {

	protected final int x;
	protected final int y;
	protected final int z;

	protected Edge e;

	@Inject
	public StopEdgeHandler(@SimArea.x int x, @SimArea.y int y, @SimArea.z int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void setEdge(Edge e) {
		this.e = e;
	}

	@Override
	public Move getValidMove(final Location loc, Move m) {
		double point;
		double direction;
		double edge;
		m = new Move(m.normalize());
		switch (e) {
		case X_MIN:
			point = loc.getX();
			direction = m.getX();
			edge = 0;
			break;
		case Y_MIN:
			point = loc.getY();
			direction = m.getY();
			edge = 0;
			break;
		case Z_MIN:
			point = loc.getZ();
			direction = m.getZ();
			edge = 0;
			break;
		case X_MAX:
			point = loc.getX();
			direction = m.getX();
			edge = x;
			break;
		case Y_MAX:
			point = loc.getY();
			direction = m.getY();
			edge = y;
			break;
		case Z_MAX:
			point = loc.getZ();
			direction = m.getZ();
			edge = z;
			break;
		default:
			throw new RuntimeException("Edge not initialised!");
		}

		double lamba = (edge - point) / direction;

		if (lamba <= 0)
			throw new RuntimeException("lamba <= 0!");

		return new Move(m.scalarMultiply(lamba));
	}

}
