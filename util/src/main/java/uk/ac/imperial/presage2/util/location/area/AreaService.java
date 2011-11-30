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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.StateTransformer;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironment;
import uk.ac.imperial.presage2.util.location.Cell;
import uk.ac.imperial.presage2.util.location.Location;

import com.google.inject.Inject;

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

	private final int xSize;
	private final int ySize;
	private final int zSize;

	@Inject
	public AreaService(EnvironmentSharedStateAccess sharedState, HasArea area) {
		super(sharedState);
		this.area = area;
		this.xSize = Math.max(this.area.getArea().x, 1);
		this.ySize = Math.max(this.area.getArea().y, 1);
		this.zSize = Math.max(this.area.getArea().z, 1);
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
		if (!validCell(x, y, z))
			throw new RuntimeException("Cell out of bounds");
		cellAction();

		try {
			@SuppressWarnings("unchecked")
			Set<UUID> cell = (HashSet<UUID>) sharedState.getGlobal(cellKey(x, y, z));
			if (cell == null)
				cell = new HashSet<UUID>();
			cell = Collections.unmodifiableSet(cell);

			return cell;
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	public void addToCell(int x, int y, int z, final UUID aid) {
		cellAction();
		if (validCell(x, y, z)) {
			sharedState.changeGlobal(cellKey(x, y, z), new StateTransformer() {
				@SuppressWarnings("unchecked")
				@Override
				public Serializable transform(Serializable state) {
					HashSet<UUID> cell = (HashSet<UUID>) state;
					if (cell == null)
						cell = new HashSet<UUID>();
					cell.add(aid);
					return cell;
				}
			});
		} else
			throw new RuntimeException("Cell out of bounds");
	}

	public void removeFromCell(int x, int y, int z, final UUID aid) {
		cellAction();
		if (validCell(x, y, z)) {
			sharedState.changeGlobal(cellKey(x, y, z), new StateTransformer() {
				@SuppressWarnings("unchecked")
				@Override
				public Serializable transform(Serializable state) {
					HashSet<UUID> cell = (HashSet<UUID>) state;
					if (cell == null)
						cell = new HashSet<UUID>();
					cell.remove(aid);
					return cell;
				}
			});
		} else
			throw new RuntimeException("Cell out of bounds");
	}

	public int getSizeX() {
		return this.xSize;
	}

	public int getSizeY() {
		return this.ySize;
	}

	public int getSizeZ() {
		return this.zSize;
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

	private String cellKey(int x, int y, int z) {
		return "area.cell." + x + "." + y + "." + z;
	}

	private boolean validCell(int x, int y, int z) {
		return x >= 0 && x < xSize && y >= 0 && y < ySize && z >= 0 && z < zSize;
	}

	private void cellAction() {
		if (!isCellArea()) {
			this.cellArea = true;
		}
	}

}
