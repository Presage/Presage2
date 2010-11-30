/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

import com.google.inject.Provider;

/**
 * @author Sam Macbeth
 *
 */
public class RealNetworkConnectorFactory implements NetworkConnectorFactory {
	
	/**
	 * @see org.imperial.isn.presage2.core.network.NetworkConnectorFactory#create(java.util.UUID)
	 */
	@Override
	public NetworkConnector create(UUID id) {
		/* 
		 * TODO This method should pull the spec for the participant given by
		 * the given UUID from the simulation config and instantiate it.
		 */
		return null;
	}

}
