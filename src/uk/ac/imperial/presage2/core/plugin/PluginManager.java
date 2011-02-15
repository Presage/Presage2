/**
 * 
 */
package uk.ac.imperial.presage2.core.plugin;

import uk.ac.imperial.presage2.core.environment.EnvironmentService;
import uk.ac.imperial.presage2.core.environment.EnvironmentServiceProvider;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;

/**
 * @author Sam Macbeth
 *
 */
public class PluginManager implements EnvironmentServiceProvider {

	private final EnvironmentSharedStateAccess sharedState;
	
	private final EnvironmentServiceProvider serviceProvider;
	
	/**
	 * @param sharedState
	 * @param serviceProvider
	 */
	public PluginManager(EnvironmentSharedStateAccess sharedState,
			EnvironmentServiceProvider serviceProvider) {
		super();
		this.sharedState = sharedState;
		this.serviceProvider = serviceProvider;
	}



	public <T extends EnvironmentService> T getEnvironmentService(Class<T> type) throws UnavailableServiceException {
		return serviceProvider.getEnvironmentService(type);
	}
	
}
