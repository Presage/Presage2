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

import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.util.fsm.Action;
import uk.ac.imperial.presage2.util.fsm.Transition;

/**
 * Wraps an FSM {@link Action} to correctly cast event and entity to a
 * {@link Message} and {@link FSMConversation} respectively, and provide the
 * {@link #processMessage(Message, FSMConversation)} method to perform the
 * required actions with these objects.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class MessageAction implements Action {

	@Override
	public void execute(Object event, Object entity, Transition transition) {
		try {
			processMessage((Message) event, (FSMConversation) entity, transition);
		} catch (ClassCastException e) {
			throw new RuntimeException("Unexpected types passed to MessageAction", e);
		}
	}

	/**
	 * Execute actions associated with the state change
	 * 
	 * @param message
	 * @param conv
	 * @param transition
	 */
	public abstract void processMessage(Message message, FSMConversation conv,
			Transition transition);

}
