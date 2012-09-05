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

package uk.ac.imperial.presage2.core.network;

import java.util.List;
import java.util.UUID;

import org.junit.Test;

import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.network.BasicNetworkConnector;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.UnicastMessage;
import static org.junit.Assert.*;

public class BasicNetworkConnectorTest extends NetworkConnectorTest {

	@Override
	public void setUp() throws Exception {
		mockExpectations();
		this.testConnector = new BasicNetworkConnector(controller,
				networkAddressFactory, addressUuid);
	}

	@Test
	public void testMessagesClearedAfterRetrieving() {
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

		final List<Message> messages2 = testConnector.getMessages();
		// check this list is empty
		assertTrue(messages2.size() == 0);
	}

}
