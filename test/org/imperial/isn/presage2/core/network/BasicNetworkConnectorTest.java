package org.imperial.isn.presage2.core.network;

import java.util.List;
import java.util.UUID;

import org.imperial.isn.presage2.core.messaging.Performative;
import org.junit.Test;
import static org.junit.Assert.*;


public class BasicNetworkConnectorTest extends NetworkConnectorTest {

	@Override
	public void setUp() throws Exception {
		this.testConnector = new BasicNetworkConnector(controller, logger, networkAddressFactory, addressUuid);
	}
	
	@Test
	public void testMessagesClearedAfterRetrieving() {
		// create a message to send
		Message m = new UnicastMessage(Performative.CANCEL, new NetworkAddress(new UUID(rand.nextLong(), rand.nextLong())), networkAddressFactory.create(addressUuid), time);
		
		// deliver it
		testConnector.deliverMessage(m);
		// check we can retrieve it
		final List<Message> messages = testConnector.getMessages();
		// check we have only this message
		assertTrue(messages.size() == 1);
		assertEquals(messages.get(0), m);
		
		final List<Message> messages2 = testConnector.getMessages();
		// check this list is empty
		assertTrue(messages2.size() == 0);
	}

}
