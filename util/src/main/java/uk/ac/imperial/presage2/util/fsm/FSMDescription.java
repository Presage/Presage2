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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A description of an {@link FSM}. This is the data structure we use to
 * traverse the state machine.
 * 
 * @author Sam Macbeth
 * 
 */
public class FSMDescription {

	private boolean built = false;

	private Map<String, State> states = new HashMap<String, State>();
	private String startState = null;

	private Map<String, Transition> transitions = new HashMap<String, Transition>();

	private void throwIfBuilt() throws FSMException {
		if (built) {
			throw new FSMException("Attempt to modify FSMDescription which has been built.");
		}
	}

	/**
	 * Add a state to the FSM.
	 * 
	 * @param name
	 * @param type
	 * @return this
	 * @throws FSMException
	 */
	public FSMDescription addState(final String name, final StateType type) throws FSMException {
		throwIfBuilt();
		if (name != null && !states.containsKey(name)) {
			if (type == StateType.START) {
				if (startState == null)
					startState = name;
				else
					throw new FSMException("Could not add " + name + " as start state. State "
							+ startState + " is already assigned start state");
			}
			states.put(name, new State(name, type));
		} else {
			throw new FSMException("Could not add state " + name
					+ ". State with that name already exists.");
		}
		return this;
	}

	public FSMDescription addState(final Enum<?> name, final StateType type) throws FSMException {
		return addState(name.name(), type);
	}

	/**
	 * Add a state of {@link StateType#ACTIVE} to the FSM.
	 * 
	 * @param name
	 * @return this
	 * @throws FSMException
	 */
	public FSMDescription addState(final String name) throws FSMException {
		return this.addState(name, StateType.ACTIVE);
	}

	public FSMDescription addState(final Enum<?> name) throws FSMException {
		return addState(name.name());
	}

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
	 * @throws FSMException
	 */
	public FSMDescription addTransition(final String name, TransitionCondition condition,
			final String start, final String end, Action action) throws FSMException {
		throwIfBuilt();
		if (this.transitions.containsKey(name))
			throw new FSMException("Could not add transition " + name
					+ ". A transition with the same name already exists.");
		if (!this.states.containsKey(start))
			throw new FSMException("Could not add transition " + name + ". Start state " + start
					+ " does not exist");
		if (!this.states.containsKey(end))
			throw new FSMException("Could not add transition " + name + ". End state " + end
					+ " does not exist");
		if (getState(start).getType() == StateType.END) {
			throw new FSMException("End state cannot be origin of transitions");
		}
		if (condition == null)
			condition = TransitionCondition.ALWAYS;
		if (action == null)
			action = Action.NOOP;
		Transition t = new Transition(name, getState(start), getState(end), condition, action);
		this.transitions.put(name, t);
		getState(start).addTransition(t);
		return this;
	}

	public FSMDescription addTransition(final Enum<?> name, TransitionCondition condition,
			final Enum<?> start, final Enum<?> end, Action action) throws FSMException {
		return addTransition(name.name(), condition, start.name(), end.name(), action);
	}

	/**
	 * Set the {@link TransitionCondition} associated with
	 * <code>transitionName</code>. Will overwrite the existing condition for
	 * this transition.
	 * 
	 * @param transitionName
	 * @param condition
	 * @return this
	 * @throws FSMException
	 */
	public FSMDescription setTransitionCondition(String transitionName,
			TransitionCondition condition) throws FSMException {
		throwIfBuilt();
		Transition t = getTransition(transitionName);
		if (t == null)
			throw new FSMException("Transition " + transitionName + " does not exist.");
		t.setCondition(condition);
		return this;
	}

	public FSMDescription setTransitionCondition(Enum<?> transitionName,
			TransitionCondition condition) throws FSMException {
		return setTransitionCondition(transitionName.name(), condition);
	}

	/**
	 * Set the {@link Action} associated with <code>transitionName</code>. Will
	 * overwrite the existing action for this transition.
	 * 
	 * @param transitionName
	 * @param action
	 * @return
	 * @throws FSMException
	 */
	public FSMDescription setTransitionAction(String transitionName, Action action)
			throws FSMException {
		throwIfBuilt();
		Transition t = getTransition(transitionName);
		if (t == null)
			throw new FSMException("Transition " + transitionName + " does not exist.");
		t.setAction(action);
		return this;
	}

	public FSMDescription setTransitionAction(Enum<?> transitionName, Action action)
			throws FSMException {
		return setTransitionAction(transitionName.name(), action);
	}

	/**
	 * Builds the FSM, making this description immutable.
	 * 
	 * TODO The point of this function needs to be better defined. It should
	 * create a copy of the description into an immutable container separate
	 * from this instance.
	 * 
	 * @return this
	 * @throws FSMException
	 */
	public synchronized FSMDescription build() throws FSMException {
		throwIfBuilt();
		this.built = true;
		this.states = Collections.unmodifiableMap(this.states);
		this.transitions = Collections.unmodifiableMap(this.transitions);
		return this;
	}

	State getState(String name) {
		return this.states.get(name);
	}

	Transition getTransition(String name) {
		return this.transitions.get(name);
	}

	State getStartState() {
		return getState(startState);
	}

}
