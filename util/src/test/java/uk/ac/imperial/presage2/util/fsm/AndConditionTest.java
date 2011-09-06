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

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class AndConditionTest {

	private static final TransitionCondition NEVER = new TransitionCondition() {
		@Override
		public boolean allow(Object event, Object entity, State state) {
			return false;
		}
	};

	@Test
	public void test() {

		final Random rand = new Random();
		final int conditionCount = rand.nextInt(20) + 1; // int 1-20

		// initialise array of conditions to all ALWAYS
		final TransitionCondition[] alwaysTrue = new TransitionCondition[conditionCount];
		for (int i = 0; i < alwaysTrue.length; i++) {
			alwaysTrue[i] = TransitionCondition.ALWAYS;
		}

		// allow should return true for all conditions return true (arguments
		// are irrelevant)
		AndCondition condition = new AndCondition(alwaysTrue);
		assertTrue(condition.allow(null, null, null));

		// try 10 different random combinations of true and false conditions
		// should always return false
		for (int i = 0; i < 10; i++) {
			TransitionCondition[] mixed = Arrays.copyOf(alwaysTrue, alwaysTrue.length);
			int elementsToChange = rand.nextInt(mixed.length) + 1;
			for (int j = 0; j < elementsToChange; j++) {
				mixed[rand.nextInt(mixed.length)] = NEVER;
			}
			condition = new AndCondition(mixed);
			assertFalse(condition.allow(null, null, null));
		}

		// all false conditions, should return false
		final TransitionCondition[] alwaysFalse = new TransitionCondition[conditionCount];
		Arrays.fill(alwaysFalse, NEVER);
		condition = new AndCondition(alwaysFalse);
		assertFalse(condition.allow(null, null, null));
	}

}
