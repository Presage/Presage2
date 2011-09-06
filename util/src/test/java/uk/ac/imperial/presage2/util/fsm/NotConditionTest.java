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
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class NotConditionTest {

	private static class TestEvent {
	};

	private static final TestEvent event = new TestEvent();

	private static class MockEntity {
	};

	private static final MockEntity entity = new MockEntity();

	@Test
	public void test() {

		final Mockery context = new Mockery();
		final TransitionCondition mockCondition = context.mock(TransitionCondition.class);
		final State mockState = new State("test", StateType.ACTIVE);

		NotCondition n1 = new NotCondition(mockCondition);

		// mockCondition returning true
		context.checking(new Expectations() {
			{
				oneOf(mockCondition).allow(with(event), with(entity), with(mockState));
				will(returnValue(true));
			}
		});
		assertFalse(n1.allow(event, entity, mockState));
		context.assertIsSatisfied();

		// mockCondition returning false
		context.checking(new Expectations() {
			{
				oneOf(mockCondition).allow(with(event), with(entity), with(mockState));
				will(returnValue(false));
			}
		});
		assertTrue(n1.allow(event, entity, mockState));
		context.assertIsSatisfied();

	}

}
