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

package uk.ac.imperial.presage2.core.util.random;

/**
 * Factory Constructor for {@link Random}.
 * 
 * @author Sam Macbeth
 * 
 */
class RandomFactory {

	/**
	 * <p>
	 * Creates an instance of {@link Random}
	 * </p>
	 * <p>
	 * Currently simply uses the default constructor, in future we will allow it
	 * to pull a previously specified random seed to initialise the Random with
	 * </p>
	 * 
	 * @return
	 */
	protected static Random create() {
		if (Random.seed > 0)
			return new Random(Random.seed);
		else
			return new Random();
	}

}
