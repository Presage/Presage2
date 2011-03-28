/**
 * 
 */
package uk.ac.imperial.presage2.core.network;

import java.util.ArrayList;
import java.util.List;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.messaging.Performative;

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

	protected List<NetworkAddress> to;

	/**
	 * <p>Create a MulticastMessage with empty recipients list.</p>
	 * @param performative
	 * @param from
	 * @param timestamp
	 */
	public MulticastMessage(Performative performative, NetworkAddress from, Time timestamp) {
		super(performative, from, timestamp);
		this.to = new ArrayList<NetworkAddress>();
	}

	/**
	 * Create a MulticastMessage with provided list of recipients.
	 * @param performative
	 * @param from
	 * @param to
	 * @param timestamp
	 */
	public MulticastMessage(Performative performative, NetworkAddress from, List<NetworkAddress> to, 
			Time timestamp) {
		super(performative, from, timestamp);
		this.to = to;
	}
	
	/**
	 * Add a single recipient to the list.
	 * @param recipient
	 */
	public void addRecipient(NetworkAddress recipient) {
		this.to.add(recipient);
	}
	
	public void addRecipients(List<NetworkAddress> recipients) {
		this.to.addAll(recipients);
	}
	
	public List<NetworkAddress> getTo() {
		return this.to;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.network.Message#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() +": (Time: "+this.timestamp.toString()+", from: "+this.from.toString()+", to: "+this.to.size()+" recipients, perf: "+this.performative.toString() +")";
	}
	
}
