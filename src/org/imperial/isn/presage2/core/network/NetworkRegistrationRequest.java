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

	protected NetworkAddress id;
	
	protected NetworkChannel link;

	/**
	 * @param id
	 */
	public NetworkRegistrationRequest(NetworkAddress id, NetworkChannel link) {
		super();
		this.id = id;
		this.link = link;
	}

	/**
	 * @return the id
	 */
	public NetworkAddress getAddress() {
		return id;
	}

	/**
	 * @return the link
	 */
	public NetworkChannel getLink() {
		return link;
	}
	
}
