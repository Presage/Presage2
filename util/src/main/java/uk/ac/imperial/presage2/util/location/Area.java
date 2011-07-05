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

import com.google.inject.AbstractModule;

public interface Area {

	public boolean contains(Location l);


	/**
	 * Provides various {@link AbstractModule}s to bind different
	 * types of {@link Area}.
	 * @author Sam Macbeth
	 *
	 */
	class Bind {

		/**
		 * Bind a 2D simulation area ({@link Area2D}) with size x, y.
		 * @param x limit of area
		 * @param y limit of area
		 * @return {@link AbstractModule} which will bind {@link Area} to {@link Area2D}
		 * 		and it's x & y values to the provided x and y.
		 */
		public static AbstractModule area2D(final int x, final int y) {
			return new AbstractModule() {
				@Override
				protected void configure() {
					bind(Area.class).to(Area2D.class);
					bind(Integer.class).annotatedWith(SimSize.x.class).toInstance(x);
					bind(Integer.class).annotatedWith(SimSize.y.class).toInstance(y);
				}
			};
		}

	}

}
