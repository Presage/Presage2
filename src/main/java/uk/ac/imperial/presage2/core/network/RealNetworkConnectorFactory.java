/**
 * 
 */
package uk.ac.imperial.presage2.core.network;

import java.util.UUID;

/**
 * @author Sam Macbeth
 *
 */
public class RealNetworkConnectorFactory implements NetworkConnectorFactory {
	
	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkConnectorFactory#create(java.util.UUID)
	 */
	@Override
	public NetworkConnector create(UUID id) {
		/* 
		 * TODO This method should pull the spec for the participant given by
		 * the given UUID from the simulation config and instantiate it.
		 * 
		 * This would also be a good time to register the NetworkConnector with the 
		 * NetworkController.
		 */
		return null;
	}

}
