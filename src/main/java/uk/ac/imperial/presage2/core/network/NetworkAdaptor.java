/**
 * 
 */
package uk.ac.imperial.presage2.core.network;

import java.util.List;

/**
 * <p>A network adaptor is a participant's perception
 * of it's communication channel to the outside world.</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface NetworkAdaptor {

	/**
	 * Gets inputs currently queued at this adaptor.
	 * @return list of messages received.
	 */
	public List<Message> getMessages();
	
	/**
	 * Send a message
	 * @param m
	 */
	public void sendMessage(Message m);
	
	/**
	 * Gets this device's network address
	 * @return this device's network address
	 */
	public NetworkAddress getAddress();
	
}
