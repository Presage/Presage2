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

}
