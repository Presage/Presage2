package org.imperial.isn.presage2.core.network;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.imperial.isn.presage2.core.IntegerTime;
import org.imperial.isn.presage2.core.Time;
import org.imperial.isn.presage2.core.messaging.Performative;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NetworkControllerTest {
	
	// RNG
	final Random rand = new Random();
	
	Logger logger = Logger.getLogger(NetworkController.class);
	
	Mockery context = new Mockery();
	
	// mocked time
	final Time time = context.mock(Time.class);
	
	// the networkcontroller
	NetworkController testController;
	
	// mock 4 network channels for use in the tests.
	final NetworkChannel channel1 = context.mock(NetworkChannel.class, "networkchannel1");
	final NetworkChannel channel2 = context.mock(NetworkChannel.class, "networkchannel2");
	final NetworkChannel channel3 = context.mock(NetworkChannel.class, "networkchannel3");
	final NetworkChannel channel4 = context.mock(NetworkChannel.class, "networkchannel4");
	
	// address of the channels
	final NetworkAddress channel1Address = new NetworkAddress(
			new UUID(rand.nextLong(), rand.nextLong()));
	final NetworkAddress channel2Address = new NetworkAddress(
			new UUID(rand.nextLong(), rand.nextLong()));
	final NetworkAddress channel3Address = new NetworkAddress(
			new UUID(rand.nextLong(), rand.nextLong()));
	final NetworkAddress channel4Address = new NetworkAddress(
			new UUID(rand.nextLong(), rand.nextLong()));
	
	// registration requests for channels
	final NetworkRegistrationRequest regRequest1 = new NetworkRegistrationRequest(channel1Address, channel1);
	final NetworkRegistrationRequest regRequest2 = new NetworkRegistrationRequest(channel2Address, channel2);
	final NetworkRegistrationRequest regRequest3 = new NetworkRegistrationRequest(channel3Address, channel3);
	final NetworkRegistrationRequest regRequest4 = new NetworkRegistrationRequest(channel4Address, channel4);
	
	/**
	 * Creates a new NetworkController so we can guarentee it's state has been
	 * reset, and adds initial expectations.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		testController = new NetworkController(logger, time);
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testNetworkController() {
		assertNotNull(testController);
	}
	
	/**
	 * Test to ensure the NetworkController does not log any errors/warnings
	 * when incrementing time with no devices connected and that the time variable
	 * is incremented.
	 */
	@Test
	public void testIncrementTime() {
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
		}});
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testDeliverMessageWithNoRegisteredChannels() {
		
		final Message undeliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, channel2Address, channel1Address, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			exactly(1).of(time).increment();
			never(channel1).deliverMessage(undeliveredMessage);
		}});
		// test if we can send a message to this address
		testController.deliverMessage(undeliveredMessage);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testRegisterConnectorAllowsMessageDelivery() {
		
		final Message deliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, channel2Address, channel1Address, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			exactly(1).of(time).increment();
			one(channel1).deliverMessage(deliveredMessage);
		}});
		// test if we can send a message to this address
		testController.registerConnector(regRequest1);
		testController.deliverMessage(deliveredMessage);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testDeliverMessageDoesNotRetroactivelyDeliver() {
		
		final Message undeliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, channel2Address, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			exactly(2).of(time).increment();
			never(channel2).deliverMessage(undeliveredMessage);
		}});
		// test if we can send a message to this address
		testController.deliverMessage(undeliveredMessage);
		testController.incrementTime();
		testController.registerConnector(regRequest2);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testMessageNotDeliveredInSameCycle() {
		
		final Message undeliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, channel2Address, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			never(time).increment();
			never(channel2).deliverMessage(undeliveredMessage);
		}});
		testController.registerConnector(regRequest2);
		testController.deliverMessage(undeliveredMessage);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testUniCastDelivery() {
		
		// message from channel1 to channel2
		final Message message = new UnicastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, channel2Address, time);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// channel1 should not receive, channel2 receives 1
			never(channel1).deliverMessage(message);
			one(channel2).deliverMessage(message);
			never(channel3).deliverMessage(message);
		}});
		
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		testController.registerConnector(regRequest3);
		testController.deliverMessage(message);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testMultiCastDelivery() {
		
		// message from channel1 to channel2 and 3
		final MulticastMessage message = new MulticastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, time);
		message.addRecipient(channel2Address);
		message.addRecipient(channel3Address);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// channel1 should not receive, channel2 and 3 receives 1
			never(channel1).deliverMessage(message);
			one(channel2).deliverMessage(message);
			one(channel3).deliverMessage(message);
			never(channel4).deliverMessage(message);
		}});
		
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		testController.registerConnector(regRequest3);
		testController.registerConnector(regRequest4);
		testController.deliverMessage(message);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testBroadCastDelivery() {
		
		// message from channel1 to channel2 and 3
		final Message message = new BroadcastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, time);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// channel1 should not receive, channel2 receives 1
			never(channel1).deliverMessage(message);
			one(channel2).deliverMessage(message);
			one(channel3).deliverMessage(message);
		}});
		
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		testController.registerConnector(regRequest3);
		testController.deliverMessage(message);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void testRegisterConnectorFailure() {
		
		try {
			testController.registerConnector(null);
			fail("NetworkController.registerConnector() should throw an exception when argument is null");
		} catch(RuntimeException e) {	}
		
		// test reg request with null args
		NetworkRegistrationRequest regRequest = new NetworkRegistrationRequest(null, null);
		try {
			testController.registerConnector(regRequest);
			fail("NetworkController.registerConnector() should throw an exception when request has null args");
		} catch(RuntimeException e) {	}
		
		// test reg request with null id
		regRequest = new NetworkRegistrationRequest(null, channel1);
		try {
			testController.registerConnector(regRequest);
			fail("NetworkController.registerConnector() should throw an exception when request has null args");
		} catch(RuntimeException e) {	}
		
		// test reg request with null channel
		regRequest = new NetworkRegistrationRequest(channel1Address, null);
		try {
			testController.registerConnector(regRequest);
			fail("NetworkController.registerConnector() should throw an exception when request has null args");
		} catch(RuntimeException e) {	}
		
		// valid reg request
		regRequest = new NetworkRegistrationRequest(channel1Address, channel1);
		testController.registerConnector(regRequest);
	}
	
	/**
	 * Test than an exception is thrown when a multicast message has one
	 * or more non registered recipients.
	 */
	@Test
	public void testMulticastToOneUnreachable() {
		
		// register connectors
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		
		// build multicast message
		final MulticastMessage message = new MulticastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, time);
		
		// test with 1 invalid recipient
		message.addRecipient(channel3Address);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// noone should receive anything.
			never(channel1).deliverMessage(message);
			never(channel2).deliverMessage(message);
			never(channel3).deliverMessage(message);
		}});
		
		testController.deliverMessage(message);
		testController.incrementTime();
		
		context.assertIsSatisfied();
		
	}
	
	@Test
	public void testMulticastToMultipleUnreachable() {
		// register connectors
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		
		// build multicast message
		final MulticastMessage message = new MulticastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, time);
		
		// test with 2 invalid recipients
		message.addRecipient(channel3Address);
		message.addRecipient(channel4Address);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// noone should receive.
			never(channel1).deliverMessage(message);
			never(channel2).deliverMessage(message);
			never(channel3).deliverMessage(message);
			never(channel4).deliverMessage(message);
		}});
		
		testController.deliverMessage(message);
		testController.incrementTime();
		
		context.assertIsSatisfied();
		
	}
	
	@Test
	public void testMulticastUnreachableDoesNotBlockValid() {
		// register connectors
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		
		// build multicast message
		final MulticastMessage message = new MulticastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, time);
		
		// test with 2 invalid recipients & 1 valid
		message.addRecipient(channel3Address);
		message.addRecipient(channel2Address);
		message.addRecipient(channel4Address);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// noone should receive.
			never(channel1).deliverMessage(message);
			one(channel2).deliverMessage(message);
			never(channel3).deliverMessage(message);
			never(channel4).deliverMessage(message);
		}});
		
		testController.deliverMessage(message);
		testController.incrementTime();
		
		context.assertIsSatisfied();
	}
	
	/* This test needs changing as exception is caught inside incrementTime so not visible
	 * to test...
	@Test
	public void testUnknownMessageType() {
		
		class MockMessage extends Message {

			public MockMessage(Performative performative, NetworkAddress from,
					Time timestamp) {
				super(performative, from, timestamp);
			}
			
		}
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
		}});
		
		final Message message = new MockMessage(Performative.AGREE, channel1Address, time);
		
		try {
			testController.deliverMessage(message);
			testController.incrementTime();
			fail("NetworkController.deliverMessage() should throw an exception when passed an unknown message type");
		} catch(UnknownMessageTypeException e) {}
		
		context.assertIsSatisfied();
	}*/

}
