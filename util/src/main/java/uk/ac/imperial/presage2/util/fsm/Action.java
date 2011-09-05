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
 * An FSM Action is something which is executed when a transition is taken.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Action {

	/**
	 * A no-op Action which does nothing.
	 */
	public static final Action NOOP = new Action() {
		@Override
		public void execute(Object event, Object entity, Transition transition) {
		}
	};

	/**
	 * Executes this action for the {@link Transition} <code>transition</code>
	 * caused by <code>event</code>.
	 * 
	 * @param event
	 *            the event which caused this transition.
	 * @param entity
	 *            Entity object for this FSM.
	 * @param transition
	 *            transition being taken.
	 */
	public void execute(Object event, Object entity, Transition transition);

}
