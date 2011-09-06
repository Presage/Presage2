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
 * Decides whether a transition may be made from a state given an event.
 * 
 * @author Sam Macbeth
 * 
 */
public interface TransitionCondition {

	public static final TransitionCondition ALWAYS = new TransitionCondition() {
		@Override
		public boolean allow(Object event, Object entity, State state) {
			return true;
		}
	};

	/**
	 * Test whether this state transition is allowed given the event object and
	 * current state.
	 * 
	 * @param event
	 *            The event which is being applied on the state machine.
	 * @param entity
	 *            Entity associated with this state machine.
	 * @param state
	 *            Current state of the FSM.
	 * @return true if this transition is allowed from <code>state</code> given
	 *         <code>event</code>
	 */
	public boolean allow(Object event, Object entity, State state);

}
