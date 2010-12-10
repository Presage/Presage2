/**
 * 
 */
package org.imperial.isn.presage2.core.network;

/**
 * @author Sam Macbeth
 *
 */
public class UnknownMessageTypeException extends NetworkException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6075812255856879414L;
	
	final protected Message message;

	/**
	 * @param message
	 * @param message2
	 */
	public UnknownMessageTypeException(Message message) {
		super("Unknown message: "+ message.toString());
		this.message = message;
	}
	
}
