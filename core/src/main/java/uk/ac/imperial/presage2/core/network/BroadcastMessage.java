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
import uk.ac.imperial.presage2.core.messaging.Performative;

/**
 * <p>
 * This is a basic broadcast message to be sent between agents.
 * </p>
 * 
 * <p>
 * This message will only send a performative. If you want to send any
 * objects/data with the message you should extend this class.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class BroadcastMessage extends Message {

	/**
	 * @param performative
	 * @param from
	 * @param timestamp
	 */
	public BroadcastMessage(Performative performative, NetworkAddress from,
			Time timestamp) {
		super(performative, from, timestamp);
	}

	/**
	 * 
	 * @param performative
	 * @param from
	 * @param timestamp
	 * @param data
	 */
	public BroadcastMessage(Performative performative, NetworkAddress from,
			Time timestamp, Object data) {
		super(performative, from, timestamp, data);
	}

	/**
	 * @param performative
	 * @param type
	 * @param timestamp
	 * @param from
	 * @param data
	 */
	public BroadcastMessage(Performative performative, String type,
			Time timestamp, NetworkAddress from, Object data) {
		super(performative, type, timestamp, from, data);
	}

	public BroadcastMessage(Performative performative, String type,
			Time timestamp, NetworkAddress from) {
		super(performative, type, timestamp, from);
	}

}
