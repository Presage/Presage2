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

import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.util.network.Message;
import uk.ac.imperial.presage2.util.network.NetworkAddress;

/**
 * A Conversation is a stateful exchange of messages between 2 or more agents.
 * 
 * @author Sam Macbeth
 * 
 */
public interface Conversation {

	/**
	 * @return The conversation's unique key.
	 */
	UUID getID();

	/**
	 * @return The state of this conversation.
	 */
	String getState();

	/**
	 * @return True if the conversation has been completed.
	 */
	boolean isFinished();

	/**
	 * Get your role in this conversation
	 * 
	 * @return
	 */
	Role getRole();

	/**
	 * 
	 * @return {@link NetworkAddress}es of all the participants in this
	 *         conversation
	 */
	Set<NetworkAddress> getMembers();

	boolean canHandle(Message in);

	void handle(Message in);

}
