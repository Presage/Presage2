/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

/**
 * Object to pass parameters to the NetworkController
 * when registering with the network.
 * 
 * @author Sam Macbeth
 *
 */
public class NetworkRegistrationRequest {

	protected UUID id;
	
	protected NetworkChannel link;

	/**
	 * @param id
	 */
	public NetworkRegistrationRequest(UUID id, NetworkChannel link) {
		super();
		this.id = id;
		this.link = link;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @return the link
	 */
	public NetworkChannel getLink() {
		return link;
	}
	
}
