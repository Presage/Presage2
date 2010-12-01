/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

import org.imperial.isn.presage2.core.Time;
import org.imperial.isn.presage2.core.messaging.Performative;

/**
 * <p>This is a basic unicast message to be sent between agents. </p>
 * 
 * <p>This message will only send a performative. If you want to
 * send any objects/data with the message you should extend
 * this class.</p>
 * 
 * @author Sam Macbeth
 *
 */
public class UnicastMessage extends Message {

	/**
	 * Intended recipient of this message.
	 */
	protected NetworkAddress to;

	/**
	 * @param performative
	 * @param from
	 * @param timestamp
	 * @param to
	 */
	public UnicastMessage(Performative performative, NetworkAddress from, NetworkAddress to, Time timestamp) {
		super(performative, from, timestamp);
		this.to = to;
	}
	
	/**
	 * Gets the intended recipient of this message.
	 * @return UUID recipient.
	 */
	public NetworkAddress getTo() {
		return this.to;
	}

	/**
	 * @see org.imperial.isn.presage2.core.network.Message#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() +": (Time: "+this.timestamp.toString()+", from: "+this.from.toString()+", to: "+this.to.toString()+", perf: "+this.performative.toString() +")";
	}
	
	
	
}
