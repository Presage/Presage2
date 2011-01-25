package uk.ac.imperial.presage2.core.environment;

import com.google.inject.Inject;

/**
 * <p>An EnvironmentService provides a high level service to a Participant and/or
 * the NetworkController by accessing the raw data in the environment shared state.</p>
 * 
 * <p>This provides an abstraction layer to the shared state as well as protection of shared
 * state data (performing object copys when necessary) and access limitations as required</p>
 */
abstract public class EnvironmentService {

	protected EnvironmentSharedStateAccess sharedState;

	/**
	 * @param sharedState
	 */
	@Inject
	protected EnvironmentService(EnvironmentSharedStateAccess sharedState) {
		super();
		this.sharedState = sharedState;
	}

}
