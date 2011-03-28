/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

/**
 * A class implementing this can provide {@link EnvironmentService}s
 * 
 * @author Sam Macbeth
 *
 */
public interface EnvironmentServiceProvider {

	public <T extends EnvironmentService> T getEnvironmentService(Class<T> type) throws UnavailableServiceException;
	
}
