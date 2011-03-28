/**
 * 
 */
package uk.ac.imperial.presage2.core.network;

import java.util.List;

import uk.ac.imperial.presage2.core.TimeDriven;

/**
 * <p>A network adaptor which provides additional services for
 * discovering nodes in the network</p>
 * 
 * @author Sam Macbeth
 *
 */
public interface NetworkAdaptorWithNodeDiscovery extends NetworkAdaptor, TimeDriven {

	/**
	 * The network adaptor may also provide a network node discovery
	 * service which we describe in the following form.
	 * @return List of UUIDs of connected nodes.
	 * @throws NetworkException
	 */
	public List<NetworkAddress> getConnectedNodes();
	
}
