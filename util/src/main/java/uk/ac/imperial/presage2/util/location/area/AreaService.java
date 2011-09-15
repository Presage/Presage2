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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.SharedState;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironment;
import uk.ac.imperial.presage2.util.location.Cell;
import uk.ac.imperial.presage2.util.location.Location;

/**
 * <p>
 * Global environment service to get the simulation {@link Area} as defined by
 * the environment and provided through {@link HasArea}.
 * </p>
 * 
 * <h3>Usage</h3>
 * 
 * <p>
 * If you're extending {@link AbstractEnvironment} simply add an instance of
 * this service to the global environment services in
 * {@link AbstractEnvironment#initialiseGlobalEnvironmentServices}.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class AreaService extends EnvironmentService {

	private HasArea area;

	private boolean cellArea = false;

	@Inject
	public AreaService(EnvironmentSharedStateAccess sharedState, HasArea area) {
		super(sharedState);
		this.area = area;
	}

	/**
	 * Get the simulation {@link Area}
	 * 
	 * @return simulation {@link Area}
	 */
	public Area getSimulationArea() {
		return ((HasArea) sharedState.getGlobal("area").getValue()).getArea();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise(Map<String, SharedState<?>> globalSharedState) {
		globalSharedState.put("area", new SharedState<HasArea>("area", this.area));
		globalSharedState.put(
				"area.cells",
				new SharedState<Set<UUID>[][][]>("area.cells", new Set[Math.max(
						this.area.getArea().x, 1)][Math.max(this.area.getArea().y, 1)][Math.max(
						this.area.getArea().z, 1)]));
	}

	/**
	 * Get the cell corresponding to the coordinates <code>x,y,z</code>. This
	 * cell contains the set of agents currently in this cell.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Set<UUID> getCell(int x, int y, int z) {
		cellArea = true;
		@SuppressWarnings("unchecked")
		Set<UUID>[][][] cellMap = (Set<UUID>[][][]) sharedState.getGlobal("area.cells").getValue();

		try {
			Set<UUID> cell = cellMap[x][y][z];
			if (cell == null)
				cell = cellMap[x][y][z] = new HashSet<UUID>();

			return cell;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}
	}

	public int getSizeX() {
		return this.area.getArea().x;
	}

	public int getSizeY() {
		return this.area.getArea().y;
	}

	public int getSizeZ() {
		return Math.max(this.area.getArea().z, 1);
	}

	/**
	 * Returns true if {@link Cell}s are being used instead of basic
	 * {@link Location}s.
	 * 
	 * @return
	 */
	public boolean isCellArea() {
		return cellArea;
	}

}
