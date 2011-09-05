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
package uk.ac.imperial.presage2.util.fsm;

/**
 * {@link TransitionCondition} which returns true iff the java class type of the
 * event is the same as the type this class is instaniated with.
 * 
 * @author Sam Macbeth
 * 
 */
public class EventTypeCondition implements TransitionCondition {

	private final Class<?> type;

	public EventTypeCondition(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return true iff <code>event</code> is an instance of {@link #type}
	 */
	@Override
	public boolean allow(Object event, Object entity, State state) {
		return (type.isInstance(event));
	}

}
