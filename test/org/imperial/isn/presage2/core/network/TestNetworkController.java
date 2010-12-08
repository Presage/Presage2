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

public class TestNetworkController {

	//NetworkController testController;
	
	Logger logger;
	
	@Before
	public void setUp() throws Exception {
		logger = Logger.getLogger(NetworkController.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNetworkController() {
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		NetworkController testController = new NetworkController(logger, time);
		assertNotNull(testController);
	}
	
	/**
	 * Test to ensure the NetworkController does not log any errors/warnings
	 * when incrementing time with no devices connected and that the time variable
	 * is incremented.
	 */
	@Test
	public void testIncrementTime() {
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		NetworkController testController = new NetworkController(logger, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
		}});
		testController.incrementTime();
	}
	
	@Test
	public void testDeliverMessageWithNoRegisteredChannels() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel = context.mock(NetworkChannel.class);
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create the register request
		final NetworkAddress channelAddress = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest = 
			new NetworkRegistrationRequest(channelAddress, channel);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
		final NetworkAddress messageSender = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final Message undeliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, messageSender, channelAddress, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			exactly(1).of(time).increment();
			never(channel).deliverMessage(undeliveredMessage);
		}});
		// test if we can send a message to this address
		testController.deliverMessage(undeliveredMessage);
		testController.incrementTime();
		
	}
	
	@Test
	public void testRegisterConnectorAllowsMessageDelivery() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel = context.mock(NetworkChannel.class);
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create the register request
		final NetworkAddress channelAddress = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest = 
			new NetworkRegistrationRequest(channelAddress, channel);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
		final NetworkAddress messageSender = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final Message deliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, messageSender, channelAddress, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			exactly(1).of(time).increment();
			one(channel).deliverMessage(deliveredMessage);
		}});
		// test if we can send a message to this address
		testController.registerConnector(regRequest);
		testController.deliverMessage(deliveredMessage);
		testController.incrementTime();
	}
	
	@Test
	public void testDeliverMessageDoesNotRetroactivelyDeliver() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel = context.mock(NetworkChannel.class);
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create the register request
		final NetworkAddress channelAddress = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest = 
			new NetworkRegistrationRequest(channelAddress, channel);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
		final NetworkAddress messageSender = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final Message undeliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, messageSender, channelAddress, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			exactly(2).of(time).increment();
			never(channel).deliverMessage(undeliveredMessage);
		}});
		// test if we can send a message to this address
		testController.deliverMessage(undeliveredMessage);
		testController.incrementTime();
		testController.registerConnector(regRequest);
		testController.incrementTime();
	}
	
	@Test
	public void testMessageNotDeliveredInSameCycle() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel = context.mock(NetworkChannel.class);
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create the register request
		final NetworkAddress channelAddress = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest = 
			new NetworkRegistrationRequest(channelAddress, channel);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
		final NetworkAddress messageSender = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final Message undeliveredMessage = new UnicastMessage(Performative.ACCEPT_PROPOSAL, messageSender, channelAddress, time);
		context.checking(new Expectations() {{
			// make sure time is incremented
			never(time).increment();
			never(channel).deliverMessage(undeliveredMessage);
		}});
		
		testController.registerConnector(regRequest);
		testController.deliverMessage(undeliveredMessage);
	}
	
	@Test
	public void testUniCastDelivery() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel1 = context.mock(NetworkChannel.class, "networkchannel1");
		final NetworkChannel channel2 = context.mock(NetworkChannel.class, "networkchannel2");
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create 2 register requests
		final NetworkAddress channel1Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest1 = 
			new NetworkRegistrationRequest(channel1Address, channel1);
		// 
		final NetworkAddress channel2Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest2 = 
			new NetworkRegistrationRequest(channel2Address, channel2);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
		// message from channel1 to channel2
		final Message message = new UnicastMessage(Performative.ACCEPT_PROPOSAL, channel1Address, channel2Address, time);
		
		context.checking(new Expectations() {{
			// make sure time is incremented
			one(time).increment();
			// channel1 should not receive, channel2 receives 1
			never(channel1).deliverMessage(message);
			one(channel2).deliverMessage(message);
		}});
		
		testController.registerConnector(regRequest1);
		testController.registerConnector(regRequest2);
		testController.deliverMessage(message);
		testController.incrementTime();
	}
	
	@Test
	public void testMultiCastDelivery() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel1 = context.mock(NetworkChannel.class, "networkchannel1");
		final NetworkChannel channel2 = context.mock(NetworkChannel.class, "networkchannel2");
		final NetworkChannel channel3 = context.mock(NetworkChannel.class, "networkchannel3");
		final NetworkChannel channel4 = context.mock(NetworkChannel.class, "networkchannel4");
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create 4 register requests
		final NetworkAddress channel1Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest1 = 
			new NetworkRegistrationRequest(channel1Address, channel1);
		// 
		final NetworkAddress channel2Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest2 = 
			new NetworkRegistrationRequest(channel2Address, channel2);
		//
		final NetworkAddress channel3Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest3 = 
			new NetworkRegistrationRequest(channel3Address, channel3);
		//
		final NetworkAddress channel4Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest4 = 
			new NetworkRegistrationRequest(channel4Address, channel4);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
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
	}
	
	@Test
	public void testBroadCastDelivery() {
		// set up the mocks
		Mockery context = new Mockery();
		final Time time = context.mock(Time.class);
		final NetworkChannel channel1 = context.mock(NetworkChannel.class, "networkchannel1");
		final NetworkChannel channel2 = context.mock(NetworkChannel.class, "networkchannel2");
		final NetworkChannel channel3 = context.mock(NetworkChannel.class, "networkchannel3");
		
		// create controller
		NetworkController testController = new NetworkController(logger, time);
		
		// create 3 register requests
		final NetworkAddress channel1Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest1 = 
			new NetworkRegistrationRequest(channel1Address, channel1);
		// 
		final NetworkAddress channel2Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest2 = 
			new NetworkRegistrationRequest(channel2Address, channel2);
		//
		final NetworkAddress channel3Address = new NetworkAddress(
				new UUID(new Random().nextLong(), new Random().nextLong()));
		final NetworkRegistrationRequest regRequest3 = 
			new NetworkRegistrationRequest(channel3Address, channel3);
		
		// allow cloning of time by the message constructor
		context.checking(new Expectations() {{
			allowing(time).clone(); will(returnValue(time));
		}});
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
	}
	/*
	 * TODO:
	 * Test register connector exception
	 */

}
