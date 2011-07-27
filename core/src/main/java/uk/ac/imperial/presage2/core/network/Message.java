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
import uk.ac.imperial.presage2.core.messaging.Input;
import uk.ac.imperial.presage2.core.messaging.Performative;

/**
 * <p>
 * This is a basic message to be sent between agents.
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
public abstract class Message<T> implements Input {

	/**
	 * FIPA performative of the message
	 */
	protected final Performative performative;

	/**
	 * Timestamp of when this message was sent.
	 */
	protected Time timestamp;

	/**
	 * Send of this message
	 */
	protected NetworkAddress from;

	protected T data;

	/**
	 * @param performative
	 * @param from
	 * @param timestamp
	 */
	public Message(Performative performative, NetworkAddress from,
			Time timestamp) {
		super();
		this.performative = performative;
		this.timestamp = timestamp.clone();
		this.from = from;
		this.data = null;
	}

	public Message(Performative performative, NetworkAddress from,
			Time timestamp, T data) {
		super();
		this.performative = performative;
		this.timestamp = timestamp.clone();
		this.from = from;
		this.data = data;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.messaging.Input#getPerformative()
	 */
	@Override
	public Performative getPerformative() {
		return this.performative;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.messaging.Input#getTimestamp()
	 */
	@Override
	public Time getTimestamp() {
		return this.timestamp;
	}

	/**
	 * 
	 * @see uk.ac.imperial.presage2.core.messaging.Input#setTimestamp(uk.ac.imperial.presage2.core.Time)
	 */
	@Override
	public void setTimestamp(Time t) {
		this.timestamp = t;
	}

	/**
	 * Get the sender of this message
	 * 
	 * @return UUID of message sender.
	 */
	public NetworkAddress getFrom() {
		return this.from;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": (Time: "
				+ this.timestamp.toString() + ", from: " + this.from.toString()
				+ ", perf: " + this.performative.toString() + ")";
	}

	/**
	 * Get the data in this message.
	 * 
	 * @return
	 */
	protected T getData() {
		return data;
	}

}
