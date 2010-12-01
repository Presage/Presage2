/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import org.imperial.isn.presage2.core.Time;
import org.imperial.isn.presage2.core.messaging.Performative;

/**
 * <p>This is a basic broadcast message to be sent between agents. </p>
 * 
 * <p>This message will only send a performative. If you want to
 * send any objects/data with the message you should extend
 * this class.</p>
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
	public BroadcastMessage(Performative performative, NetworkAddress from, Time timestamp) {
		super(performative, from, timestamp);
	}

}
