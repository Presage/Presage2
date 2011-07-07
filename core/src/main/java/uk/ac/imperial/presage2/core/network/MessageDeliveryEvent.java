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
package uk.ac.imperial.presage2.core.network;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.event.Event;

/**
 * Event trigger when a message is successfully delivered.
 * 
 * @author Sam Macbeth
 * 
 * @param <S>
 */
public final class MessageDeliveryEvent implements Event {

	final protected Time time;
	final protected Message message;
	final protected NetworkAddress recipient;

	MessageDeliveryEvent(Time time, Message message, NetworkAddress recipient) {
		super();
		this.time = time;
		this.message = message;
		this.recipient = recipient;
	}

	@Override
	public Time getTime() {
		return time;
	}

	public final Message getMessage() {
		return message;
	}

	public final NetworkAddress getRecipient() {
		return recipient;
	}

}
