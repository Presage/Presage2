/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sam Macbeth
 *
 */
public class UnreachableRecipientException extends NetworkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4463569707723015287L;

	final protected Message message;
	
	final protected List<NetworkAddress> recipients;

	/**
	 * @param message
	 * @param recipient
	 * @param e
	 */
	public UnreachableRecipientException(Message message,
			NetworkAddress recipient, Throwable e) {
		super("Unable to send message "+message.toString()+" to recipient "+ recipient.toString(), e);
		this.message = message;
		this.recipients = new ArrayList<NetworkAddress>();
		this.recipients.add(recipient);
	}
	
	public UnreachableRecipientException(Message message, List<NetworkAddress> recipients) {
		super("Unable to send message "+message.toString()+" to "+recipients.size()+" recipients");
		this.message = message;
		this.recipients = recipients;
	}
	
}
