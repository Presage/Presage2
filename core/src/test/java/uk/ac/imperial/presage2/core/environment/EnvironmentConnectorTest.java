/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

/**
 * 
 */
package uk.ac.imperial.presage2.core.environment;


import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.Action;
import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.participant.Participant;
import uk.ac.imperial.presage2.core.util.random.Random;

/**
 * 
 * This is a general test case for the contract implied by any class
 * implementing the {@link EnvironmentConnector} interface.
 * 
 * @author Sam Macbeth
 *
 */
public abstract class EnvironmentConnectorTest {

	Mockery context = new Mockery();
	
	final Time time = context.mock(Time.class);
	
	/**
	 * Generic {@link Action} you can use with {@link EnvironmentConnectorTest#aHandler} for mock
	 * action handling.
	 */
	final Action action = new Action() {};
	/**
	 * Mocked {@link ActionHandler} for faking handle-able actions. Mock expectations with respect to
	 * handling an {@link Action} will be done against {@link EnvironmentConnectorTest#action}.
	 */
	final ActionHandler aHandler = context.mock(ActionHandler.class);
	
	final EnvironmentConnector environment = getEnvironmentConnector();
	
	final Participant participant1 = context.mock(Participant.class);
	final UUID participant1ID = Random.randomUUID();
	UUID participant1Authkey;
	
	/**
	 * Gets the environment connector to test.
	 * @return
	 */
	public abstract EnvironmentConnector getEnvironmentConnector();
	
	/**
	 * Get a registration request valid for this EnvironmentConnector
	 * @param id
	 * @param p
	 * @return
	 */
	public abstract EnvironmentRegistrationRequest getRegistrationRequest(UUID id, Participant p);
	
	/**
	 * Get an Action which this environment should be able to handle.
	 * @return
	 */
	public abstract Action getValidAction();
	
	/**
	 * Gets the types of services we expect to receive from the environment connector.
	 * @return
	 */
	public abstract Set<Class<? extends EnvironmentService>> getExpectedServices();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testUnregisteredActFailsWithValidAction() throws ActionHandlingException {
		// valid action
		context.checking(new Expectations() {{
			allowing(aHandler).canHandle(action); will(returnValue(true));
		}});
		try {
			environment.act(action, Random.randomUUID(), Random.randomUUID());
			fail("Unregistered Participant should not be able to act.");
		} catch(UnregisteredParticipantException expected) {
			
		}
	}
	
	@Test
	public void testUnregisteredActFailsWithInvalidAction() throws ActionHandlingException {
		context.checking(new Expectations() {{
			allowing(aHandler).canHandle(action); will(returnValue(false));
		}});
		try {
			environment.act(action, Random.randomUUID(), Random.randomUUID());
			fail("Unregistered Participant should not be able to act.");
		} catch(UnregisteredParticipantException expected) {
			
		}
	}
	
	/**
	 * Check when attempting to register with an invalid request (null values in request)
	 */
	@Test
	public void testRegisterFailure() throws ActionHandlingException {
		context.checking(new Expectations() {{
			allowing(participant1).getID(); will(returnValue(null));
		}});
		
		// null participant in request
		final EnvironmentRegistrationRequest req1 = getRegistrationRequest(participant1ID, null);
		try {
			environment.register(req1);
			fail("Exception not thrown by AbstractEnvironment.register() with null participant in request");
		} catch(Exception e) {
		} 
		// ensure we cannot act now.
		try {
			environment.act(getValidAction(), participant1ID, Random.randomUUID());
			fail("Unregistered Participant should not be able to act.");
		} catch(UnregisteredParticipantException e) {
			
		}
		
		// null participant ID
		final EnvironmentRegistrationRequest req2 = getRegistrationRequest(null, participant1);
		try {
			environment.register(req2);
			fail("Exception not thrown by AbstractEnvironment.register() with null participantid in request");
		} catch(Exception e) {
		}
		// ensure we cannot act now.
		try {
			environment.act(getValidAction(), participant1ID, Random.randomUUID());
			fail("Unregistered Participant should not be able to act.");
		} catch(UnregisteredParticipantException e) {
			
		}
	}
	
	@Test
	public void testDeregisterFailsWhenUnregistered() {
		
		// both params null
		try {
			environment.deregister(null, null);
			fail("Environment.deregister with null params should throw an exception.");
		} catch(UnregisteredParticipantException e) {}
		
		// null authkey
		try {
			environment.deregister(participant1ID, null);
			fail("Environment.deregister with null params should throw an exception.");
		} catch(UnregisteredParticipantException e) {}
		
		// null participant id
		try {
			environment.deregister(null, Random.randomUUID());
			fail("Environment.deregister with null params should throw an exception.");
		} catch(UnregisteredParticipantException e) {}
		
		// no nulls
		try {
			environment.deregister(participant1ID, Random.randomUUID());
			fail("Environment.deregister with null params should throw an exception.");
		} catch(UnregisteredParticipantException e) {}
		
	}
	
	@Test
	public void testValidUsageSuccess() throws ActionHandlingException {
		final Action invalidAction = new Action() {};
		// valid action
		context.checking(new Expectations() {{
			allowing(aHandler).canHandle(action); will(returnValue(true));
			allowing(aHandler).canHandle(invalidAction); will(returnValue(false));
			allowing(aHandler).handle(action, participant1ID); will(returnValue(null));
			allowing(participant1).getID(); will(returnValue(participant1ID));
		}});
		
		final EnvironmentRegistrationRequest req1 = getRegistrationRequest(participant1ID, participant1);
		
		final EnvironmentRegistrationResponse resp = environment.register(req1);
		assertNotNull(resp);
		final UUID authkey = resp.getAuthKey();
		// check we receive at least the services we expect to.
		final Set<EnvironmentService> services = resp.getServices();
		final Set<Class<? extends EnvironmentService>> expectedServices = getExpectedServices();
		for(EnvironmentService s : services) {
			if(expectedServices.contains(s.getClass())) {
				expectedServices.remove(s.getClass());
			}
		}
		assertTrue(expectedServices.size() == 0);
		
		// ensure we can act
		try {
			environment.act(getValidAction(), participant1ID, authkey);
		} catch(ActionHandlingException e) {
			fail("Valid action failed to be handled");
		}
		// null action
		try {
			environment.act(null, participant1ID, authkey);
			fail("Acting with null action did not raise an exception, ActionHandlingException expected");
		} catch(ActionHandlingException e) {}
		// unhandled action
		try {
			environment.act(invalidAction, participant1ID, authkey);
			fail("Acting with unknown action did not raise an exception, ActionHandlingException expected");
		} catch(ActionHandlingException e) {}
		
		// ensure we can deregister
		environment.deregister(participant1ID, authkey);
	}
	
	@Test
	public void testInvalidAuthkey() {
		context.checking(new Expectations() {{
			allowing(aHandler).canHandle(action); will(returnValue(true));
			allowing(participant1).getID(); will(returnValue(participant1ID));
		}});
		final EnvironmentRegistrationRequest req1 = getRegistrationRequest(participant1ID, participant1);
		final EnvironmentRegistrationResponse resp = environment.register(req1);
		assertNotNull(resp);
		final UUID authkey = resp.getAuthKey();
		// null authkey
		try {
			environment.act(getValidAction(), participant1ID, null);
		} catch(InvalidAuthkeyException e) {
			
		} catch(ActionHandlingException e) {
			fail("Valid action failed to be handled");
		}
		// random authkey
		try {
			environment.act(getValidAction(), participant1ID, Random.randomUUID());
		} catch(InvalidAuthkeyException e) {
			
		} catch(ActionHandlingException e) {
			fail("Valid action failed to be handled");
		}
	}
	

}
