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
package uk.ac.imperial.presage2.core.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.util.random.Random;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Sam Macbeth
 * 
 */
abstract public class NetworkConnectorTest {

	final Random rand = Random.getInstance();

	final Mockery context = new Mockery();

	final Injector injector = Guice.createInjector(new NetworkGuiceModule());

	final NetworkChannel controller = context.mock(NetworkChannel.class);

	final NetworkAddressFactory networkAddressFactory = context
			.mock(NetworkAddressFactory.class);

	final UUID addressUuid = new UUID(rand.nextLong(), rand.nextLong());

	final NetworkAddress testAddr = new NetworkAddress(addressUuid);

	final Time time = context.mock(Time.class);

	NetworkConnector testConnector;

	/**
	 * Instantiate this.testConnector with the network connector to test.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	abstract public void setUp() throws Exception;

	public void mockExpectations() {
		context.checking(new Expectations() {
			{
				allowing(time).clone();
				will(returnValue(time));
				allowing(networkAddressFactory).create(addressUuid);
				will(returnValue(testAddr));
			}
		});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNetworkConnector() {
		assertNotNull(testConnector);
	}

	@Test
	public void testGetAddress() {
		assertEquals(addressUuid, testConnector.getAddress().getId());
	}

	@Test
	public void testMessageDelivery() {
		// create a message to send
		Message m = new UnicastMessage(Performative.CANCEL,
				new NetworkAddress(new UUID(rand.nextLong(), rand.nextLong())),
				networkAddressFactory.create(addressUuid), time);

		// deliver it
		testConnector.deliverMessage(m);
		// check we can retrieve it
		final List<Message> messages = testConnector.getMessages();
		// check we have only this message
		assertTrue(messages.size() == 1);
		assertEquals(messages.get(0), m);
	}

	@Test
	public void testMessageSending() {
		// create a message to send
		final Message m = new UnicastMessage(Performative.CANCEL,
				networkAddressFactory.create(addressUuid), new NetworkAddress(
						new UUID(rand.nextLong(), rand.nextLong())), time);

		// create expectations
		context.checking(new Expectations() {
			{
				one(controller).deliverMessage(m);
			}
		});

		// send it
		testConnector.sendMessage(m);

		context.assertIsSatisfied();
	}

}
