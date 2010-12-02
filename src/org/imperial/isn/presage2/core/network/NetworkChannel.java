/**
 * 
 */
package org.imperial.isn.presage2.core.network;

/**
 * <p>This interface represents a channel over which communication
 * can take place in the network.</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface NetworkChannel {

	/**
	 * Deliver a message through the network channel.
	 * @param m
	 * @throws NetworkException
	 */
	public void deliverMessage(Message m);
	
	
}
