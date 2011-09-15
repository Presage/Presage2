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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Mockery;
import org.junit.Test;

public class FSMDescriptionTest {

	private enum States {
		START, END, OTHER
	};

	private enum Transitions {
		TEST
	};

	@Test
	public void testAddState() throws FSMException {
		final FSMDescription desc = new FSMDescription();

		// adding start state
		final String start = "start";
		desc.addState(start, StateType.START);

		final State startState = desc.getStartState();
		assertTrue(start.equals(startState.getName()));
		assertSame(StateType.START, startState.getType());
		assertSame(startState, desc.getState(start));

		// add an end state
		final String end = "end";
		desc.addState(end, StateType.END);

		final State endState = desc.getState(end);
		assertTrue(end.equals(endState.getName()));
		assertSame(StateType.END, endState.getType());

		// add an active state
		final String s1 = "s1";
		desc.addState(s1, StateType.ACTIVE);
		assertTrue(s1.equals(desc.getState(s1).getName()));
		assertSame(StateType.ACTIVE, desc.getState(s1).getType());

		// add state with no type - should be active
		final String s2 = "s2";
		desc.addState(s2);
		assertTrue(s2.equals(desc.getState(s2).getName()));
		assertSame(StateType.ACTIVE, desc.getState(s2).getType());

		// add an enum state
		desc.addState(States.OTHER);
		assertTrue(States.OTHER.name().equals(desc.getState(States.OTHER.name()).getName()));

		// states are case-sensitive
		assertNull(desc.getState("Start"));
		desc.addState("StarT");

		// adding duplicate state throws exception
		try {
			desc.addState(start);
			fail();
		} catch (FSMException e) {
		}

		// adding more than 1 start state throws exception
		try {
			desc.addState(States.START, StateType.START);
			fail();
		} catch (FSMException e) {
		}

		// adding more than 1 end state is fine
		desc.addState(States.END, StateType.END);

		// add null state
		try {
			String nullState = null;
			desc.addState(nullState);
			fail();
		} catch (FSMException e) {
		}

	}

	@Test
	public void testAddTransition() throws FSMException {
		final Mockery context = new Mockery();
		final FSMDescription desc = new FSMDescription();
		// adding start state
		final String start = "start";
		desc.addState(start, StateType.START);
		// add an end state
		final String end = "end";
		desc.addState(end, StateType.END);

		// add start-> end transition
		final TransitionCondition mockCondition = context.mock(TransitionCondition.class);
		final Action mockAction = context.mock(Action.class);
		final String t1name = "t1";
		desc.addTransition(t1name, mockCondition, start, end, mockAction);
		final Transition t1 = desc.getTransition(t1name);

		assertTrue(t1.getName().equals(t1name));
		assertSame(desc.getStartState(), t1.getStart());
		assertSame(desc.getState(end), t1.getEnd());
		assertSame(mockCondition, t1.getCondition());
		assertSame(mockAction, t1.getAction());
		assertTrue(desc.getStartState().getTransitions().size() == 1);
		assertTrue(desc.getStartState().getTransitions().contains(t1));

		// transition with null condition/action
		final String t2name = "t2";
		desc.addTransition(t2name, null, start, end, null);
		final Transition t2 = desc.getTransition(t2name);
		// condition should be ALWAYS, action should be NOOP
		assertSame(TransitionCondition.ALWAYS, t2.getCondition());
		assertSame(Action.NOOP, t2.getAction());

		// enum states
		desc.addState(States.END, StateType.END).addState(States.OTHER);

		// enum transition
		desc.addTransition(Transitions.TEST, mockCondition, States.OTHER, States.END, null);
		final Transition t3 = desc.getTransition(Transitions.TEST.name());

		assertTrue(t3.getName().equals(Transitions.TEST.name()));
		assertSame(desc.getState(States.OTHER.name()), t3.getStart());
		assertSame(desc.getState(States.END.name()), t3.getEnd());
		assertSame(mockCondition, t3.getCondition());
		assertSame(Action.NOOP, t3.getAction());

		// transition from end state
		try {
			desc.addTransition("from_end", null, end, States.OTHER.name(), null);
			fail();
		} catch (FSMException e) {
		}

		// duplicate transition name
		try {
			desc.addTransition(t1name, null, start, States.END.name(), null);
			fail();
		} catch (FSMException e) {
		}
	}
}
