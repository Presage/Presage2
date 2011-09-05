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

import uk.ac.imperial.presage2.util.fsm.Action;
import uk.ac.imperial.presage2.util.fsm.Transition;

/**
 * Wraps an FSM {@link Action} to cast the event and entity to a
 * {@link ConversationSpawnEvent} and {@link FSMConversation} respectively and
 * provide the
 * {@link #processSpawn(ConversationSpawnEvent, FSMConversation, Transition)}
 * method to perform actions required when the conversation is spawned.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class SpawnAction implements Action {

	@Override
	public void execute(Object event, Object entity, Transition transition) {
		try {
			processSpawn((ConversationSpawnEvent) event, (FSMConversation) entity, transition);
		} catch (ClassCastException e) {
			throw new RuntimeException("Unexpected types passed to SpawnAction", e);
		}
	}

	/**
	 * Executes actions when a conversation is spawned. Will automatically add
	 * recipients from the {@link ConversationSpawnEvent} to the converstaion
	 * recipients.
	 * 
	 * @param event
	 * @param conv
	 * @param transition
	 */
	public void processSpawn(ConversationSpawnEvent event, FSMConversation conv,
			Transition transition) {
		conv.recipients.addAll(event.getTargets());
	}

}
