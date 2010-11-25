/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

import org.imperial.isn.presage2.core.Time;
import org.imperial.isn.presage2.core.messaging.Input;
import org.imperial.isn.presage2.core.messaging.Performative;

/**
 * <p>This is a basic message to be sent between agents. </p>
 * 
 * <p>This message will only send a performative. If you want to
 * send any objects/data with the message you should extend
 * this class.</p>
 * 
 * @author Sam Macbeth
 *
 */
public class Message implements Input {

	protected final Performative performative;
	
	protected Time timestamp;
	
	protected UUID to, from;
	
	/**
	 * @param performative
	 * @param timestamp
	 * @param to
	 * @param from
	 */
	public Message(Performative performative, UUID to, UUID from, Time timestamp) {
		super();
		this.performative = performative;
		this.timestamp = timestamp.clone();
		this.to = to;
		this.from = from;
	}

	/**
	 * @see org.imperial.isn.presage2.core.messaging.Input#getPerformative()
	 */
	@Override
	public Performative getPerformative() {
		return this.performative;
	}

	/**
	 * @see org.imperial.isn.presage2.core.messaging.Input#getTimestamp()
	 */
	@Override
	public Time getTimestamp() {
		return this.timestamp;
	}

	/**
	 * 
	 * @see org.imperial.isn.presage2.core.messaging.Input#setTimestamp(org.imperial.isn.presage2.core.Time)
	 */
	@Override
	public void setTimestamp(Time t) {
		this.timestamp = t;
	}

}
