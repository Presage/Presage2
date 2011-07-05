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


/**
 *  General shared state in the environment.
 */
public class SharedState<T> {

    protected String type;

    protected T value;

    public SharedState(String type, T value) {
        this.type = type;
        this.value = value;
    }

    /**
	 * Get the name of the type of this shared state. This is a string
	 * identifier such that the environment can be queried for this state.
	 * @return {@link String} identifier of this type of shared state.
	 */
    public String getType() {
        return type;
    }

    /**
     * Get the value of this shared state.
     * @return	Object representing this state. Type dicated by templated type.
     */
    public T getValue() {
        return value;
    }

    /**
     * Set the value of this shared state to value.
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Get the {@link Class} of this shared state.
     * @return {@link Class} of the state's value.
     */
    public Class<? extends Object> getValueType() {
        return value.getClass();
    }

}
