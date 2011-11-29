package uk.ac.imperial.presage2.core.environment;

import uk.ac.imperial.presage2.core.TimeDriven;

/**
 * Classes implementing this can act as a storage layer for the shared state of
 * a simulation. It combines access and modification of
 * {@link EnvironmentSharedStateAccess} with {@link TimeDriven} to control when
 * state changes and committed.
 * 
 * @author Sam Macbeth
 * 
 */
public interface SharedStateStorage extends EnvironmentSharedStateAccess, TimeDriven {
}
