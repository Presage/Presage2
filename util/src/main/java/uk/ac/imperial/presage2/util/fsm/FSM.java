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

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Instance of a Finite State Machine. Takes a {@link FSMDescription} describing
 * the state machine which then can be executed with called to
 * {@link #applyEvent(Object)}.
 * </p>
 * 
 * <p>
 * The state machine is described with a set of {@link State}s and
 * {@link Transition}s. Transitions have {@link TransitionCondition}s which
 * determine whether a transition should be taken for a given event. If a
 * transition is taken the {@link Action} associated with it will be executed.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class FSM {

	private final FSMDescription desc;
	private final Object entity;

	private final Set<StateChangeListener> listeners = new HashSet<StateChangeListener>();

	private State currentState;

	/**
	 * @param desc
	 * @param entity
	 */
	public FSM(FSMDescription desc, Object entity) {
		super();
		this.desc = desc;
		this.entity = entity;
		this.currentState = this.desc.getStartState();
	}

	public static FSMDescription description() {
		return new FSMDescription();
	}

	public String getState() {
		return currentState.getName();
	}

	public Object getEntity() {
		return entity;
	}

	public boolean canApplyEvent(Object event) {
		for (Transition t : this.currentState.getTransitions()) {
			if (t.getCondition().allow(event, entity, currentState)) {
				return true;
			}
		}
		return false;
	}

	public void applyEvent(Object event) throws FSMException {
		Set<Transition> possible = new HashSet<Transition>();
		for (Transition t : this.currentState.getTransitions()) {
			if (t.getCondition().allow(event, entity, currentState)) {
				possible.add(t);
			}
		}
		int count = possible.size();
		if (count > 1) {
			// multiple paths, pick arbitrarily.
			// TODO more deterministic behaviour
			for (Transition t : possible) {
				doTransition(event, t);
				break;
			}
		} else if (count == 1) {
			for (Transition t : possible) {
				doTransition(event, t);
				break;
			}
		} else {
			// no available transition, throw exception
			throw new FSMException("No transition available from this state with this event.");
		}
	}

	private void doTransition(Object e, Transition t) {
		t.getAction().execute(e, entity, t);

		for (StateChangeListener l : this.listeners) {
			l.onStateChange(entity, this.currentState, t.getEnd());
		}
		this.currentState = t.getEnd();
	}

	public boolean isStartState() {
		return currentState.getType() == StateType.START;
	}

	public boolean isEndState() {
		return currentState.getType() == StateType.END;
	}

	public void addListener(StateChangeListener listener) {
		this.listeners.add(listener);
	}

}
