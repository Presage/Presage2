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
package uk.ac.imperial.presage2.util.protocols;

import static org.junit.Assert.assertEquals;

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
import uk.ac.imperial.presage2.core.simulator.Step;
import uk.ac.imperial.presage2.util.environment.AbstractEnvironmentModule;
import uk.ac.imperial.presage2.util.fsm.Action;
import uk.ac.imperial.presage2.util.fsm.AndCondition;
import uk.ac.imperial.presage2.util.fsm.EventTypeCondition;
import uk.ac.imperial.presage2.util.fsm.FSMDescription;
import uk.ac.imperial.presage2.util.fsm.StateType;
import uk.ac.imperial.presage2.util.fsm.Transition;
import uk.ac.imperial.presage2.util.network.BasicNetworkConnector;
import uk.ac.imperial.presage2.util.network.BroadcastMessage;
import uk.ac.imperial.presage2.util.network.Message;
import uk.ac.imperial.presage2.util.network.NetworkAdaptor;
import uk.ac.imperial.presage2.util.network.NetworkAddress;
import uk.ac.imperial.presage2.util.network.NetworkModule;
import uk.ac.imperial.presage2.util.network.UnicastMessage;
import uk.ac.imperial.presage2.util.participant.AbstractParticipant;

public class TestFSMProtocol {

	FSMDescription ping;
	final Set<NetworkAddress> responders = new HashSet<NetworkAddress>();

	@Before
	public void setUp() throws Exception {
		responders.clear();
		ping = new FSMDescription();
		ping.addState("START", StateType.START);
		ping.addState("BROADCAST");
		ping.addState("AWAIT_RESPONSE");
		ping.addState("END", StateType.END);

		ping.addTransition("spawn", new EventTypeCondition(
				ConversationSpawnEvent.class), "START", "AWAIT_RESPONSE",
				new SpawnAction() {
					@Override
					public void processSpawn(ConversationSpawnEvent event,
							FSMConversation conv, Transition transition) {
						try {
							conv.getNetwork().sendMessage(
									new BroadcastMessage(Performative.CFP,
											"ping", conv.getLastTransition(),
											conv.getNetwork().getAddress()));
						} catch (ActionHandlingException e) {
							e.printStackTrace();
						}
					}
				});
		ping.addTransition("response", new AndCondition(
				new ConversationCondition(), new MessageTypeCondition("pong")),
				"AWAIT_RESPONSE", "AWAIT_RESPONSE", new MessageAction() {
					@Override
					public void processMessage(Message message,
							FSMConversation conv, Transition transition) {
						responders.add(message.getFrom());
					}
				});
		ping.addTransition("timeout", new TimeoutCondition(4),
				"AWAIT_RESPONSE", "END", new Action() {
					@Override
					public void execute(Object event, Object entity,
							Transition transition) {
					}
				});

		ping.addTransition("pong", new MessageTypeCondition("ping"), "START",
				"END", new InitialiseConversationAction() {

					@Override
					public void processInitialMessage(Message message,
							FSMConversation conv, Transition transition) {
						try {
							conv.getNetwork().sendMessage(
									new UnicastMessage(Performative.INFORM,
											"pong", conv.getLastTransition(),
											conv.getNetwork().getAddress(),
											message.getFrom()));
						} catch (ActionHandlingException e) {
							e.printStackTrace();
						}
					}
				});
		ping.build();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws UnavailableServiceException {
		final ConversationalAgent p1 = new ConversationalAgent(
				UUID.randomUUID(), "p1", ping);
		final ConversationalAgent p2 = new ConversationalAgent(
				UUID.randomUUID(), "p2", ping);
		final ConversationalAgent p3 = new ConversationalAgent(
				UUID.randomUUID(), "p3", ping);
		RunnableSimulation sim = new RunnableSimulation() {
			@Override
			public void initialiseScenario(Scenario s) {
				addModule(new AbstractEnvironmentModule()
						.addParticipantEnvironmentService(BasicNetworkConnector.class));
				addModule(NetworkModule.fullyConnectedNetworkModule());
				s.addAgent(p1);
				s.addAgent(p2);
				s.addAgent(p3);
			}
		};

		sim.initialise();

		sim.step();

		p1.proto.spawn();

		sim.step();
		assertEquals(0, responders.size());
		sim.step();
		sim.step();
		assertEquals(2, responders.size());
		sim.step();
		sim.step();
		sim.step();
		assertEquals(2, responders.size());
	}

	public static class ConversationalAgent extends AbstractParticipant {

		FSMDescription desc;
		FSMProtocol proto;
		NetworkAdaptor network;

		public ConversationalAgent(UUID id, String name, FSMDescription desc) {
			super(id, name);
			this.desc = desc;
		}

		@Step
		public void step(int t) throws UnavailableServiceException {
			if (network == null) {
				network = getEnvironmentService(BasicNetworkConnector.class);
				proto = new FSMProtocol("ping", desc, network);
			}
			for (Message m : network.getMessages()) {
				if (proto.canHandle(m)) {
					proto.handle(m);
				}
			}
			proto.incrementTime(t);
			network.getMessages().clear();
		}

	}

}
