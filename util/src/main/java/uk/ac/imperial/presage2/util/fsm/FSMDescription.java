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

public interface FSMDescription<E> {

	/**
	 * Add a state to the FSM.
	 * 
	 * @param name
	 * @param type
	 * @return this
	 */
	FSMDescription<E> addState(String name, StateType type);

	/**
	 * Add a state of {@link StateType#ACTIVE} to the FSM.
	 * 
	 * @param name
	 * @return this
	 */
	FSMDescription<E> addState(String name);

	/**
	 * Add a transition with {@link TransitionCondition} <code>condition</code>
	 * between states <code>start</code> and <code>end</code> to the FSM which
	 * executes the {@link Action} <code>action</code>.
	 * 
	 * @param name
	 *            Name of the transition.
	 * @param condition
	 *            {@link TransitionCondition} on when this transition is taken.
	 *            May be <code>null</code> in which case
	 *            {@link TransitionCondition#ALWAYS} will be used unless it is
	 *            changed by
	 *            {@link #setTransitionCondition(String, TransitionCondition)}
	 * @param start
	 *            Name of the start node (must exist already in the
	 *            {@link FSMDescription})
	 * @param end
	 *            Name of the end node (must exist already in the
	 *            {@link FSMDescription})
	 * @param action
	 *            {@link Action} to perform when this tranition is taken. May be
	 *            <code>null</code> in which case a noop will be used unless it
	 *            is set by {@link #setTransitionAction(String, Action)}
	 * @return this
	 */
	FSMDescription<E> addTransition(String name, TransitionCondition<? extends E> condition,
			String start, String end, Action<? extends E> action);

	State getState(String name);

	Transition getTransition(String name);

	/**
	 * Set the {@link TransitionCondition} associated with
	 * <code>transitionName</code>. Will overwrite the existing condition for
	 * this transition. May be called after {@link #build()} has been called.
	 * 
	 * @param transitionName
	 * @param condition
	 * @return this
	 */
	FSMDescription<E> setTransitionCondition(String transitionName,
			TransitionCondition<? extends E> condition);

	/**
	 * Set the {@link Action} associated with <code>transitionName</code>. Will
	 * overwrite the existing action for this transition. May be called after
	 * {@link #build()} has been called.
	 * 
	 * @param transitionName
	 * @param action
	 * @return
	 */
	FSMDescription<E> setTransitionAction(String transitionName, Action<? extends E> action);

	/**
	 * Builds the FSM, making this description immutable.
	 * 
	 * @return this
	 */
	FSMDescription<E> build();
}
