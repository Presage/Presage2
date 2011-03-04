package uk.ac.imperial.presage2.core.environment;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.*;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.participant.Participant;

public class AbstractEnvironmentConnectorTest extends EnvironmentConnectorTest {
	
	@Override
	public EnvironmentConnector getEnvironmentConnector() {
		return new AbstractEnvironment() {

			@Override
			public <T extends EnvironmentService> T getEnvironmentService(
					Class<T> type) throws UnavailableServiceException {
				return null;
			}

			@Override
			protected Set<ActionHandler> initialiseActionHandlers() {
				Set<ActionHandler> handlers = new HashSet<ActionHandler>();
				handlers.add(aHandler);
				return handlers;
			}

			@Override
			protected Set<EnvironmentService> generateServices(
					Participant participant) {
				return new HashSet<EnvironmentService>();
			}
			
		};
	}

	@Override
	public EnvironmentRegistrationRequest getRegistrationRequest(UUID id,
			Participant p) {
		return new EnvironmentRegistrationRequest(id, p);
	}

	@Override
	public Action getValidAction() {
		return action;
	}

	@Override
	public Set<Class<? extends EnvironmentService>> getExpectedServices() {
		return new HashSet<Class<? extends EnvironmentService>>();
	}

}
