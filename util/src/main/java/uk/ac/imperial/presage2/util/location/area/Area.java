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

import java.util.HashMap;
import java.util.Map;

import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.location.Move;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;

/**
 * Represents the simulation area.
 * 
 * @author Sam Macbeth
 * 
 */
public class Area implements HasArea {

	final int x;
	final int y;
	final int z;

	protected Map<Edge, EdgeHandler> edgeHandlers;

	public enum Edge {
		X_MIN, Y_MIN, Z_MIN, X_MAX, Y_MAX, Z_MAX
	}

	@Inject
	public Area(@SimArea.x int x, @SimArea.y int y, @SimArea.z int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.addEdgeHandlers(new HashMap<Edge, EdgeHandler>());
	}

	@Inject(optional = true)
	public void addEdgeHandlers(Map<Edge, EdgeHandler> edgeHandlers) {
		this.edgeHandlers = new HashMap<Edge, EdgeHandler>(edgeHandlers);
		for (Edge e : Edge.values()) {
			if (!this.edgeHandlers.containsKey(e))
				this.edgeHandlers.put(e, new ExceptionEdgeHandler());
			this.edgeHandlers.get(e).setEdge(e);
		}
	}

	/**
	 * Check if the given {@link Location} falls inside of this area.
	 * 
	 * @param l
	 * @return True if <code>l</code> is within the area defined by this
	 *         {@link Area} object, false otherwise.
	 */
	public boolean contains(Location l) {
		return l.getX() >= 0 && l.getX() <= x && l.getY() >= 0 && l.getY() <= y
				&& l.getZ() >= 0 && l.getZ() <= z;
	}

	/**
	 * <p>
	 * If the {@link Move} <code>m</code> from {@link Location} <code>l</code>
	 * results in a Location outside of this area we create and return a
	 * modified {@link Move} based on the edge-case rules defined for this
	 * {@link Area}.
	 * </p>
	 * 
	 * <p>
	 * The edge rules are defined for all 6 possible edges of the area via
	 * {@link EdgeHandler}s. We determine which edge has been crossed then
	 * invoke the appropriate handler to give us the modified move. By default
	 * all edges have an {@link ExceptionEdgeHandler}.
	 * </p>
	 * 
	 * @param loc
	 * @param m
	 * @return if <code>loc.add(m)</code> returns a {@link Location}
	 *         <code>l</code> such that <code>this.contains(l) == false</code>
	 *         returns a <code>m</code> modified according to the
	 *         {@link EdgeHandler} on the edge which has been crossed. otherwise
	 *         returns <code>m</code>
	 */
	public Move getValidMove(final Location loc, Move m) {
		Location target = new Location(loc.add(m));
		if (target.getX() < 0) {
			m = getHandler(Edge.X_MIN).getValidMove(loc, m);
			target = new Location(loc.add(m));
		}
		if (target.getY() < 0) {
			m = getHandler(Edge.Y_MIN).getValidMove(loc, m);
			target = new Location(loc.add(m));
		}
		if (target.getZ() < 0) {
			m = getHandler(Edge.Z_MIN).getValidMove(loc, m);
			target = new Location(loc.add(m));
		}
		if (target.getX() > x) {
			m = getHandler(Edge.X_MAX).getValidMove(loc, m);
			target = new Location(loc.add(m));
		}
		if (target.getY() > y) {
			m = getHandler(Edge.Y_MAX).getValidMove(loc, m);
			target = new Location(loc.add(m));
		}
		if (target.getZ() > z) {
			m = getHandler(Edge.Z_MAX).getValidMove(loc, m);
			target = new Location(loc.add(m));
		}
		return m;
	}

	private EdgeHandler getHandler(Edge e) {
		return this.edgeHandlers.get(e);
	}

	/**
	 * Provides various {@link AbstractModule}s to bind different types of
	 * {@link Area}.
	 * 
	 * @author Sam Macbeth
	 * 
	 */
	public static class Bind extends AbstractModule {

		private final int x;
		private final int y;
		private final int z;
		private final Map<Edge, Class<? extends EdgeHandler>> edges = new HashMap<Edge, Class<? extends EdgeHandler>>();

		public Bind(int x, int y, int z) {
			super();
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/**
		 * Bind a 2D simulation area ({@link Area2D}) with size x, y.
		 * 
		 * @param x
		 *            limit of area
		 * @param y
		 *            limit of area
		 * @return {@link AbstractModule} which will bind {@link Area} to
		 *         {@link Area2D} and it's x & y values to the provided x and y.
		 */
		public static Bind area2D(final int x, final int y) {
			return new Bind(x, y, 0);
		}

		@Override
		protected void configure() {
			bind(Area.class).in(Singleton.class);
			bind(Integer.class).annotatedWith(SimArea.x.class).toInstance(x);
			bind(Integer.class).annotatedWith(SimArea.y.class).toInstance(y);
			bind(Integer.class).annotatedWith(SimArea.z.class).toInstance(z);
			bind(HasArea.class).to(Area.class);

			MapBinder<Edge, EdgeHandler> edgeBinder = MapBinder.newMapBinder(
					binder(), Edge.class, EdgeHandler.class);
			for (Map.Entry<Edge, Class<? extends EdgeHandler>> e : edges
					.entrySet()) {
				edgeBinder.addBinding(e.getKey()).to(e.getValue());
			}
		}

		public Bind edgeHandler(Class<? extends EdgeHandler> h) {
			for (Edge e : Edge.values()) {
				edges.put(e, h);
			}
			return this;
		}

		public Bind addEdgeHander(Edge e, Class<? extends EdgeHandler> h) {
			edges.put(e, h);
			return this;
		}

	}

	@Override
	public Area getArea() {
		return this;
	}

}
