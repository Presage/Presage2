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
