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
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.environment.UnavailableServiceException;
import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;
import uk.ac.imperial.presage2.core.simulator.Scenario;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.location.Location;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class TestNetworkConstraints {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws UnavailableServiceException,
			ActionHandlingException {
		final AbstractParticipant p1 = new SituatedTestingAgent("p1",
				new Location(0, 0), 5);
		final AbstractParticipant p2 = new SituatedTestingAgent("p2",
				new Location(4, 0), 5);
		final AbstractParticipant p3 = new SituatedTestingAgent("p3",
				new Location(4, 4), 5);
		final AbstractParticipant p4 = new SituatedTestingAgent("p4",
				new Location(10, 10), 2);
		RunnableSimulation sim = new RunnableSimulation() {
			@Override
			public void initialiseScenario(Scenario s) {
				addModule(new AbstractEnvironmentModule()
						.addParticipantEnvironmentService(BasicNetworkConnector.class));
				Set<Class<? extends NetworkConstraint>> constraints = new HashSet<Class<? extends NetworkConstraint>>();
				constraints.add(NetworkRangeConstraint.class);
				addModule(NetworkModule.constrainedNetworkModule(constraints));
				addObjectClass(MessageHandler.class);
				s.addAgent(p1);
				s.addAgent(p2);
				s.addAgent(p3);
				s.addAgent(p4);
			}
		};

		sim.initialise();
		sim.step();

		NetworkConnector n1 = p1
				.getEnvironmentService(BasicNetworkConnector.class);
		NetworkConnector n2 = p2
				.getEnvironmentService(BasicNetworkConnector.class);
		NetworkConnector n3 = p3
				.getEnvironmentService(BasicNetworkConnector.class);
		NetworkConnector n4 = p4
				.getEnvironmentService(BasicNetworkConnector.class);

		// check links
		Set<NetworkAddress> l1 = n1.getConnectedNodes();
		assertTrue(l1.contains(n1.getAddress()));
		assertTrue(l1.contains(n2.getAddress()));
		assertEquals(2, l1.size());

		Set<NetworkAddress> l2 = n2.getConnectedNodes();
		assertTrue(l2.contains(n1.getAddress()));
		assertTrue(l2.contains(n2.getAddress()));
		assertTrue(l2.contains(n3.getAddress()));
		assertEquals(3, l2.size());

		Set<NetworkAddress> l3 = n3.getConnectedNodes();
		assertTrue(l3.contains(n2.getAddress()));
		assertTrue(l3.contains(n3.getAddress()));
		assertEquals(2, l3.size());

		Set<NetworkAddress> l4 = n4.getConnectedNodes();
		assertTrue(l4.contains(n4.getAddress()));
		assertEquals(1, l4.size());

		sim.step();

		// sending messages
		n3.sendMessage(new BroadcastMessage(Performative.ACCEPT_PROPOSAL, n3
				.getAddress(), 1));

		sim.step();
		assertEquals(0, n1.getMessages().size());
		assertEquals(1, n2.getMessages().size());
		assertEquals(0, n3.getMessages().size());
		assertEquals(0, n4.getMessages().size());
		n2.getMessages().clear();

		n4.sendMessage(new BroadcastMessage(Performative.ACCEPT_PROPOSAL, n4
				.getAddress(), 1));

		sim.step();
		assertEquals(0, n1.getMessages().size());
		assertEquals(0, n2.getMessages().size());
		assertEquals(0, n3.getMessages().size());
		assertEquals(0, n4.getMessages().size());

	}

	public static class SituatedTestingAgent extends AbstractParticipant {

		public State<Location> loc;
		public State<Double> commRange;

		public SituatedTestingAgent(String name, Location loc, double commRange) {
			super(UUID.randomUUID(), name);
			this.loc = new State<Location>("util.location", loc);
			this.commRange = new State<Double>("network.commrange", commRange);
		}

	}

}
