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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class FSMTest {

	private static class TestEvent {
	};

	private static final TestEvent event = new TestEvent();

	private static class MockEntity {
	};

	private static final MockEntity entity = new MockEntity();

	@Test
	public void testSingleStateNoTransitionFSM() throws FSMException {
		final String stateName = "start";

		// create description
		final FSMDescription desc = new FSMDescription().addState(stateName, StateType.START)
				.build();

		// create fsm
		final FSM fsm = new FSM(desc, entity);

		assertTrue(fsm.getState().equals(stateName));

		assertTrue(fsm.isStartState());

		assertFalse(fsm.isEndState());

		assertFalse(fsm.canApplyEvent(null));

		assertFalse(fsm.canApplyEvent(event));

		try {
			fsm.applyEvent(event);
			fail();
		} catch (FSMException e) {
		}
	}

	@Test
	public void testSingleStateSingleTransitionFSM() throws FSMException {
		final String stateName = "start";
		final Mockery context = new Mockery();
		final Action mockAction = context.mock(Action.class);

		// create description
		final FSMDescription desc = new FSMDescription()
				.addState(stateName, StateType.START)
				.addTransition("self", TransitionCondition.ALWAYS, stateName, stateName, mockAction)
				.build();

		// with ALWAYS condition we can always made the transition
		final FSM fsm = new FSM(desc, entity);

		assertTrue(fsm.getState().equals(stateName));
		assertTrue(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertTrue(fsm.canApplyEvent(event));

		// test transition invokes mockAction.execute(..)
		context.checking(new Expectations() {
			{
				one(mockAction).execute(with(event), with(entity), with(any(Transition.class)));
			}
		});
		fsm.applyEvent(event);
		context.assertIsSatisfied();
		// we should still be in start state (self transition)
		assertTrue(fsm.getState().equals(stateName));
		assertTrue(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertSame(entity, fsm.getEntity());

		// now change transition to never
		desc.getTransition("self").setCondition(new NotCondition(TransitionCondition.ALWAYS));
		final FSM fsm2 = new FSM(desc, entity);

		assertTrue(fsm2.getState().equals(stateName));
		assertTrue(fsm2.isStartState());
		assertFalse(fsm2.isEndState());
		assertFalse(fsm2.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());

		// when we apply an event with no matching transition applyEvent should
		// throw and the action is not executed.
		context.checking(new Expectations() {
			{
				never(mockAction).execute(with(anything()), with(anything()),
						with(any(Transition.class)));
			}
		});
		try {
			fsm2.applyEvent(event);
			fail();
		} catch (FSMException e) {
		} finally {
			context.assertIsSatisfied();
		}
	}

	@Test
	public void testTwoStateNoTransitionFSM() throws FSMException {

		final String startState = "start";
		final String endState = "end";

		final FSMDescription desc = new FSMDescription().addState(startState, StateType.START)
				.addState(endState, StateType.END).build();

		// create fsm
		final FSM fsm = new FSM(desc, entity);

		assertTrue(fsm.getState().equals(startState));
		assertTrue(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertFalse(fsm.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());

		try {
			fsm.applyEvent(event);
			fail();
		} catch (FSMException e) {
		}
	}

	@Test
	public void testTwoStateSingleTransitionFSM() throws FSMException {
		final String startState = "start";
		final String endState = "end";

		final Mockery context = new Mockery();
		final Action mockAction = context.mock(Action.class);

		final FSMDescription desc = new FSMDescription()
				.addState(startState, StateType.START)
				.addState(endState, StateType.END)
				.addTransition("test", TransitionCondition.ALWAYS, startState, endState, mockAction)
				.build();

		// create fsm
		final FSM fsm = new FSM(desc, entity);

		// assert we are in start state
		assertTrue(fsm.getState().equals(startState));
		assertTrue(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertTrue(fsm.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());

		// test transition invokes mockAction.execute(..)
		context.checking(new Expectations() {
			{
				one(mockAction).execute(with(event), with(entity), with(any(Transition.class)));
			}
		});
		fsm.applyEvent(event);
		context.assertIsSatisfied();
		// we should be in the end state.
		assertTrue(fsm.getState().equals(endState));
		assertFalse(fsm.isStartState());
		assertTrue(fsm.isEndState());
		assertSame(entity, fsm.getEntity());

		// assert we cannot transition from end state
		try {
			fsm.applyEvent(event);
			fail();
		} catch (FSMException e) {
		}
	}

	/**
	 * Two mutually exclusive transitions, ensure correct one is taken.
	 * 
	 * @throws FSMException
	 */
	@Test
	public void testDoubleTransitionFSM() throws FSMException {
		final String startState = "start";
		final String endState = "end";
		final String badState = "bad";

		final Mockery context = new Mockery();
		final Action expectedAction = context.mock(Action.class, "expected");
		final Action unexpectedAction = context.mock(Action.class, "unexpected");

		final FSMDescription desc = new FSMDescription()
				.addState(startState, StateType.START)
				.addState(endState, StateType.END)
				.addState(badState, StateType.END)
				.addTransition("expected", TransitionCondition.ALWAYS, startState, endState,
						expectedAction)
				.addTransition("unexpected", new NotCondition(TransitionCondition.ALWAYS),
						startState, badState, unexpectedAction).build();

		final FSM fsm = new FSM(desc, entity);
		// assert we are in start state
		assertTrue(fsm.getState().equals(startState));
		assertTrue(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertTrue(fsm.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());

		// test transition invokes mockAction.execute(..) on expected, and not
		// on unexpected
		context.checking(new Expectations() {
			{
				one(expectedAction).execute(with(event), with(entity), with(any(Transition.class)));
				never(unexpectedAction).execute(with(anything()), with(anything()),
						with(any(Transition.class)));
			}
		});
		fsm.applyEvent(event);
		context.assertIsSatisfied();
		assertTrue(fsm.getState().equals(endState));
		assertFalse(fsm.isStartState());
		assertTrue(fsm.isEndState());
		assertSame(entity, fsm.getEntity());

		try {
			fsm.applyEvent(event);
			fail();
		} catch (FSMException e) {
		}
	}

	public void testMultiStageFSM() throws FSMException {
		final String startState = "start";
		final String endState = "end";
		final String interState = "intermediate";

		final Mockery context = new Mockery();
		final Action action1 = context.mock(Action.class, "action1");
		final Action action2 = context.mock(Action.class, "action2");

		final FSMDescription desc = new FSMDescription().addState(startState, StateType.START)
				.addState(endState, StateType.END).addState(interState)
				.addTransition("t1", TransitionCondition.ALWAYS, startState, interState, action1)
				.addTransition("t2", TransitionCondition.ALWAYS, interState, endState, action2)
				.build();

		final FSM fsm = new FSM(desc, entity);

		// assert we are in start state
		assertTrue(fsm.getState().equals(startState));
		assertTrue(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertTrue(fsm.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());

		// first transition: action1 executed, action2 not.
		context.checking(new Expectations() {
			{
				one(action1).execute(with(event), with(entity), with(any(Transition.class)));
				never(action2).execute(with(anything()), with(anything()),
						with(any(Transition.class)));
			}
		});
		fsm.applyEvent(event);
		context.assertIsSatisfied();
		// should be in intermediate state
		assertTrue(fsm.getState().equals(interState));
		assertFalse(fsm.isStartState());
		assertFalse(fsm.isEndState());
		assertTrue(fsm.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());

		// second transition: action2 execute, action1 not
		context.checking(new Expectations() {
			{
				never(action1).execute(with(anything()), with(anything()),
						with(any(Transition.class)));
				one(action2).execute(with(event), with(entity), with(any(Transition.class)));
			}
		});
		fsm.applyEvent(event);
		context.assertIsSatisfied();
		// should be in end state
		assertTrue(fsm.getState().equals(endState));
		assertFalse(fsm.isStartState());
		assertTrue(fsm.isEndState());
		assertFalse(fsm.canApplyEvent(event));
		assertSame(entity, fsm.getEntity());
	}

}
