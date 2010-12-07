/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * <p>An address of a device in the network.</p>
 * 
 * <p>Using this we can send messages to this device</p>
 * 
 * @author Sam Macbeth
 *
 */
public class NetworkAddress {

	/**
	 * Unique UUID of device we're sending the message to. Likely
	 * to be a participant's uuid.
	 */
	final protected UUID id;
	
	/**
	 * Create a NetworkAddress given this UUID.
	 * @param id
	 */
	@Inject
	protected NetworkAddress( @Assisted UUID id) {
		if(id == null) {
			throw new NullPointerException("Attempting to instantiate a NetworkAddress with null id.");
		}
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NetworkAddress: "+this.id.toString();
	}
	
}
