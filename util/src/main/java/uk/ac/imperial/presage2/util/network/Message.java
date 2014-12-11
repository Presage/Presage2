/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

package uk.ac.imperial.presage2.util.network;

import java.util.UUID;

import uk.ac.imperial.presage2.core.Action;
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
public abstract class Message implements Action {

	/**
	 * FIPA performative of the message
	 */
	protected final Performative performative;

	protected String type;

	/**
	 * Timestamp of when this message was sent.
	 */
	protected int timestamp;

	/**
	 * Sender of this message
	 */
	final protected NetworkAddress from;

	/**
	 * Optional conversation key.
	 */
	protected UUID conversationKey;

	/**
	 * Optional protocol name.
	 */
	protected String protocol;

	protected Object data;

	/**
	 * @param performative
	 * @param from
	 * @param timestamp
	 */
	public Message(Performative performative, NetworkAddress from,
			int timestamp) {
		super();
		this.performative = performative;
		this.timestamp = timestamp;
		this.from = from;
		this.data = null;
		this.type = "";
	}

	public Message(Performative performative, NetworkAddress from,
			int timestamp, Object data) {
		super();
		this.performative = performative;
		this.timestamp = timestamp;
		this.from = from;
		this.data = data;
		this.type = "";
	}

	public Message(Performative performative, String type, int timestamp,
			NetworkAddress from, Object data) {
		super();
		this.performative = performative;
		this.type = type;
		this.timestamp = timestamp;
		this.from = from;
		this.data = data;
	}

	public Message(Performative performative, String type, int timestamp,
			NetworkAddress from) {
		super();
		this.performative = performative;
		this.type = type;
		this.timestamp = timestamp;
		this.from = from;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.messaging.Input#getPerformative()
	 */
	public Performative getPerformative() {
		return this.performative;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.messaging.Input#getTimestamp()
	 */
	public int getTimestamp() {
		return this.timestamp;
	}

	/**
	 * 
	 * @see uk.ac.imperial.presage2.core.messaging.Input#setTimestamp(uk.ac.imperial.presage2.core.Time)
	 */
	public void setTimestamp(int t) {
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
				+ this.timestamp + ", from: " + this.from.toString()
				+ ", perf: " + this.performative.toString() + ")";
	}

	/**
	 * Get the data in this message.
	 * 
	 * @return
	 */
	public Object getData() {
		return data;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public UUID getConversationKey() {
		return conversationKey;
	}

	public void setConversationKey(UUID conversationKey) {
		this.conversationKey = conversationKey;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
