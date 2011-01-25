/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;

import java.util.Set;
import java.util.UUID;

/**
 * <p>The response to a {@link EnvironmentRegistrationRequest}. This will
 * provide an {@link EnvironmentRegistrationResponse#authKey} which allows the
 * participant to act via the {@link EnvironmentConnector}.</p>
 * 
 * <p>It will also contain a set of {@link EnvironmentService}s for access of 
 * the environment's shared state</p>
 * @author Sam Macbeth
 *
 */
public class EnvironmentRegistrationResponse {

	private UUID authKey;
	
	private Set<EnvironmentService> services;

	/**
	 * @param authKey
	 * @param services
	 */
	public EnvironmentRegistrationResponse(UUID authKey,
			Set<EnvironmentService> services) {
		super();
		this.authKey = authKey;
		this.services = services;
	}

	/**
	 * @return the authKey
	 */
	public UUID getAuthKey() {
		return authKey;
	}

	/**
	 * @return the services
	 */
	public Set<EnvironmentService> getServices() {
		return services;
	}
	
}
