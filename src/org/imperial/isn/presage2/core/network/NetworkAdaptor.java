/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.List;
import java.util.UUID;

import org.imperial.isn.presage2.core.messaging.Input;

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
	 * @return
	 */
	public List<Message> getMessages();
	
	/**
	 * Send a message
	 * @param m
	 */
	public void sendMessage(Message m) throws NetworkException;
	
	/**
	 * The network adaptor may also provide a network node discovery
	 * service which we describe in the following form.
	 * @return List of UUIDs of connected nodes.
	 * @throws NetworkException
	 */
	public List<UUID> getConnectedNodes() throws NetworkException;
	
}
