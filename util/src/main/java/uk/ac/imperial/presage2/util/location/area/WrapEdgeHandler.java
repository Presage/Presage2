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

import org.apache.commons.math.geometry.Vector3D;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;

/**
 * An {@link EdgeHandler} which wraps the simulation {@link Area} so an agent
 * moving through an edge simply appears at the opposite edge.
 * 
 * @author Sam Macbeth
 * 
 */
public class WrapEdgeHandler extends StopEdgeHandler {

	@Inject
	public WrapEdgeHandler(@SimArea.x int x, @SimArea.y int y, @SimArea.z int z) {
		super(x, y, z);
	}

	@Override
	public Move getValidMove(final Location loc, Move m) {
		// get the move up to the edge of the area
		Move toEdge = super.getValidMove(loc, m);
		// get remaining move
		m = new Move(m.subtract(toEdge));

		Vector3D edgePoint = loc.add(toEdge);
		Vector3D targetEdgePoint;
		switch (e) {
		case X_MIN:
			targetEdgePoint = new Vector3D(x, edgePoint.getY(),
					edgePoint.getZ());
			break;
		case Y_MIN:
			targetEdgePoint = new Vector3D(edgePoint.getX(), y,
					edgePoint.getZ());
			break;
		case Z_MIN:
			targetEdgePoint = new Vector3D(edgePoint.getX(), edgePoint.getY(),
					z);
			break;
		case X_MAX:
			targetEdgePoint = new Vector3D(0, edgePoint.getY(),
					edgePoint.getZ());
			break;
		case Y_MAX:
			targetEdgePoint = new Vector3D(edgePoint.getX(), 0,
					edgePoint.getZ());
			break;
		case Z_MAX:
			targetEdgePoint = new Vector3D(edgePoint.getX(), edgePoint.getY(),
					0);
			break;
		default:
			throw new RuntimeException("Edge not initialised!");
		}

		// construct move
		return new Move(targetEdgePoint.subtract(loc).add(m));
	}

}
