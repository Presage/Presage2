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

import java.util.UUID;

import com.google.inject.Singleton;

/**
 * This is a wrapper for {@link java.util.Random} to provide static access to
 * most of it's methods via a singleton, and to control the initial random seed.
 * 
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class Random extends java.util.Random {

	public static long seed = 0;

	private static final long serialVersionUID = 1L;

	private static Random INSTANCE = null;

	protected Random(long seed) {
		super(seed);
	}

	protected Random() {
		super();
	}

	/**
	 * Get the instance of {@link Random}.
	 * 
	 * @return
	 */
	public static synchronized Random getInstance() {
		if (INSTANCE == null) {
			INSTANCE = RandomFactory.create();
		}
		return INSTANCE;
	}

	/**
	 * Wrapper for {@link UUID#randomUUID()}
	 * 
	 * @return
	 */
	public static UUID randomUUID() {
		return UUID.randomUUID();
	}

	/**
	 * Wrapper for {@link java.util.Random#nextInt()}
	 * 
	 * @return
	 */
	public static int randomInt() {
		return Random.getInstance().nextInt();
	}

	/**
	 * Wrapper for {@link java.util.Random#nextInt(int)}
	 * 
	 * @return
	 */
	public static int randomInt(int n) {
		return Random.getInstance().nextInt(n);
	}

	/**
	 * Wrapper for {@link java.util.Random#nextDouble()}
	 * 
	 * @return
	 */
	public static double randomDouble() {
		return Random.getInstance().nextDouble();
	}

}
