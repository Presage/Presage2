/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.*;

/**
 * @author Sam Macbeth
 *
 */
public class NetworkGuiceModule extends AbstractModule {
	
	
	/**
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		
		bind(NetworkConnectorFactory.class).to(RealNetworkConnectorFactory.class);
		
		// we will bind to a basic NetworkAddress here via a FactoryModuleBuilder for the time being.
		install(new FactoryModuleBuilder().build(NetworkAddressFactory.class));
		
	}

}
