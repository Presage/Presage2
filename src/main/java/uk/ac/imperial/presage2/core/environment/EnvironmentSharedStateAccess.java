package uk.ac.imperial.presage2.core.environment;

import java.util.UUID;

/**
 * This is the access layer to the shared state of the environment.
 */
public interface EnvironmentSharedStateAccess {

    public SharedState<?> getGlobal(String name);

    public SharedState<?> get(String name, UUID participantID);

}
