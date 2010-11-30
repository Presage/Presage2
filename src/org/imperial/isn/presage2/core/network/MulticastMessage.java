/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.imperial.isn.presage2.core.Time;
import org.imperial.isn.presage2.core.messaging.Performative;

/**
 * <p>This is a basic multicast message to be sent between agents. </p>
 * 
 * <p>This message will only send a performative. If you want to
 * send any objects/data with the message you should extend
 * this class.</p>
 * @author Sam Macbeth
 *
 */
public class MulticastMessage extends Message {

	protected List<UUID> to;

	/**
	 * <p>Create a MulticastMessage with empty recipients list.</p>
	 * @param performative
	 * @param from
	 * @param timestamp
	 */
	public MulticastMessage(Performative performative, UUID from, Time timestamp) {
		super(performative, from, timestamp);
		this.to = new ArrayList<UUID>();
	}

	/**
	 * Create a MulticastMessage with provided list of recipients.
	 * @param performative
	 * @param from
	 * @param to
	 * @param timestamp
	 */
	public MulticastMessage(Performative performative, UUID from, List<UUID> to, 
			Time timestamp) {
		super(performative, from, timestamp);
		this.to = to;
	}
	
	/**
	 * Add a single recipient to the list.
	 * @param recipient
	 */
	public void addRecipient(UUID recipient) {
		this.to.add(recipient);
	}
	
	public void addRecipients(List<UUID> recipients) {
		this.to.addAll(recipients);
	}
	
	public List<UUID> getTo() {
		return this.to;
	}
	
}
