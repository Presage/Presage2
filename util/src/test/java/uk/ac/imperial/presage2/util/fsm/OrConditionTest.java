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

import static org.junit.Assert.*;

import java.util.Random;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class OrConditionTest {

	private static class TestEvent {
	};

	private static final TestEvent event = new TestEvent();

	private static class MockEntity {
	};

	private static final MockEntity entity = new MockEntity();

	@Test
	public void test() {

		final Random rand = new Random();
		final int conditionCount = rand.nextInt(20) + 1; // int 1-20
		final Mockery context = new Mockery();
		final State mockState = new State("test", StateType.ACTIVE);

		final TransitionCondition[] conditions = new TransitionCondition[conditionCount];
		for (int i = 0; i < conditions.length; i++) {
			conditions[i] = context.mock(TransitionCondition.class, "condition" + i);
		}

		OrCondition condition = new OrCondition(conditions);

		// all conditions false. they should all be checked
		context.checking(new Expectations() {
			{
				for (int i = 0; i < conditions.length; i++) {
					oneOf(conditions[i]).allow(event, entity, mockState);
					will(returnValue(false));
				}
			}
		});
		assertFalse(condition.allow(event, entity, mockState));
		context.assertIsSatisfied();

		// all conditions true, don't all have to be checked
		context.checking(new Expectations() {
			{
				for (int i = 0; i < conditions.length; i++) {
					allowing(conditions[i]).allow(event, entity, mockState);
					will(returnValue(true));
				}
			}
		});
		assertTrue(condition.allow(event, entity, mockState));
		context.assertIsSatisfied();

		// n conditions true
		for (int i = 0; i < 10; i++) {
			context.checking(new Expectations() {
				{
					boolean result = false;
					for (int i = 0; i < conditions.length; i++) {
						boolean value = rand.nextBoolean();
						result |= value;
						// ensure at least 1 true value
						if (i == conditions.length - 1 && !result)
							value = true;

						allowing(conditions[i]).allow(event, entity, mockState);
						will(returnValue(value));
					}
				}
			});
			assertTrue(condition.allow(event, entity, mockState));
			context.assertIsSatisfied();
		}
	}

}
