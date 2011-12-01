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
package uk.ac.imperial.presage2.util.protocols;

import uk.ac.imperial.presage2.util.fsm.State;
import uk.ac.imperial.presage2.util.fsm.TransitionCondition;

/**
 * Guard which allows a transition when a {@link Timeout} event is passed and
 * the last action of this fsm was older than the timeout.
 * 
 * @author Sam Macbeth
 * 
 */
public class TimeoutCondition implements TransitionCondition {

	final int timeout;

	public TimeoutCondition(int timeout) {
		super();
		this.timeout = timeout;
	}

	@Override
	public boolean allow(Object event, Object entity, State state) {
		if (event instanceof Timeout) {
			Timeout t = (Timeout) event;
			return ((FSMConversation) entity).getLastTransition() + timeout < t.getTime();
		}
		return false;
	}

}
