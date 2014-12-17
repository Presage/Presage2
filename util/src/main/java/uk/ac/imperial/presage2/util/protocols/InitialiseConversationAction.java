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

import uk.ac.imperial.presage2.util.fsm.Transition;
import uk.ac.imperial.presage2.util.network.Message;

/**
 * An implementation of a {@link MessageAction} designed to initialise the
 * conversation on first message receipt. Will set the conversation key and
 * recipients from the given message.
 * 
 * @author Sam Macbeth
 * 
 */
public abstract class InitialiseConversationAction extends MessageAction {

	/**
	 * Initialises the conversation key and recipient of the given
	 * {@link FSMConversation} from the {@link Message} <code>message</code>.
	 * 
	 * @see uk.ac.imperial.presage2.util.protocols.MessageAction#processMessage(uk.ac.imperial.presage2.util.network.Message,
	 *      uk.ac.imperial.presage2.util.protocols.FSMConversation,
	 *      uk.ac.imperial.presage2.util.fsm.Transition)
	 */
	@Override
	public final void processMessage(Message message, FSMConversation conv, Transition transition) {
		conv.setConversationKey(message.getConversationKey());
		conv.recipients.add(message.getFrom());
		processInitialMessage(message, conv, transition);
	}

	public abstract void processInitialMessage(Message message, FSMConversation conv,
			Transition transition);

}
