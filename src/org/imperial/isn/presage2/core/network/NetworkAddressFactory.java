/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import java.util.UUID;

/**
 * @author Sam Macbeth
 *
 */
public interface NetworkAddressFactory {

	public NetworkAddress create(UUID id);
	
}
