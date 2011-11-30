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

package uk.ac.imperial.presage2.core.environment;

import java.io.Serializable;

/**
 * General shared state in the environment.
 */
public class SharedState {

	final protected String name;

	final protected Serializable value;

	public SharedState(String name, Serializable value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get the name of the type of this shared state. This is a string
	 * identifier such that the environment can be queried for this state.
	 * 
	 * @return {@link String} identifier of this type of shared state.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the value of this shared state.
	 * 
	 * @return Object representing this state. Type dicated by templated type.
	 */
	public Serializable getValue() {
		return value;
	}

}
