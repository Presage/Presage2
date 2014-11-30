/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.util.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.jmock.Mockery;
import org.junit.Test;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.EnvironmentSharedStateAccess;
import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class TestNetwork {

	final Mockery context = new Mockery();

	@Test
	public void testNetworkConnector() {
		final AbstractParticipant p1 = new AbstractParticipant(UUID.randomUUID(), "p1") {
		};
		final AbstractParticipant p2 = new AbstractParticipant(UUID.randomUUID(), "p2") {
		};
		RunnableSimulation sim = new RunnableSimulation() {
			@Override
			public void initialiseScenario(Scenario s) {
				addModule(new AbstractEnvironmentModule().addActionHandler(MessageHandler.class));
				addObjectClass(MessageHandler.class);
				s.addAgent(p1);
				s.addAgent(p2);
			}
		};
		
		sim.initialise();
		
		EnvironmentSharedStateAccess sharedState = sim.getInjector().getInstance(EnvironmentSharedStateAccess.class);
		NetworkConnector n1 = new BasicNetworkConnector(sharedState, p1);
		NetworkConnector n2 = new BasicNetworkConnector(sharedState, p2);
		
		assertEquals("network address mirrors agent ID", n1.getAddress().getId(), p1.getID());
		assertEquals("network address mirrors agent ID", n2.getAddress().getId(), p2.getID());

		sim.step();
		sim.step();
		
		assertEquals(0, n1.getMessages().size());
		assertEquals(0, n2.getMessages().size());
		
		Message m1 = new BroadcastMessage(Performative.CFP, n1.getAddress(), 1);
		try {
			p1.act(m1);
		} catch (ActionHandlingException e) {
			fail();
		}
		
		assertEquals(0, n1.getMessages().size());
		assertEquals(0, n2.getMessages().size());
		
		sim.step();
				
		assertEquals(0, n1.getMessages().size());
		assertEquals(1, n2.getMessages().size());
		assertEquals(m1, n2.getMessages().get(0));
		
	}
}
