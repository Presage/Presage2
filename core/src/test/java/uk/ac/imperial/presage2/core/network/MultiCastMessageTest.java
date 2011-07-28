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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.MulticastMessage;
import uk.ac.imperial.presage2.core.network.NetworkAddress;

/**
 * @author Sam Macbeth
 * 
 */
public class MultiCastMessageTest extends MessageTest {

	protected List<NetworkAddress> lastTo;

	/**
	 * @see uk.ac.imperial.presage2.core.network.MessageTest#getRandomMessage()
	 */
	@Override
	protected MulticastMessage<?> getRandomMessage() {
		this.lastTime = this.randomTime();
		this.lastFrom = this.randomAddress();
		this.lastTo = new ArrayList<NetworkAddress>();
		for (int i = 0; i < rand.nextInt(10); i++) {
			this.lastTo.add(this.randomAddress());
		}
		this.lastPerf = this.randomPerformative();
		return new MulticastMessage<Object>(lastPerf, lastFrom, lastTo,
				lastTime);
	}

	/**
	 * @see uk.ac.imperial.presage2.core.network.MessageTest#testMessage()
	 */
	@Override
	@Test
	public void testMessage() {
		Message<?> m = this.getRandomMessage();

		assertNotNull(m);

		// test constructor with no to args
		MulticastMessage<?> m2 = new MulticastMessage<Object>(lastPerf,
				lastFrom, lastTime);

		assertNotNull(m2);

		assertTrue(m2.to.isEmpty());
	}

	@Test
	public void testGetTo() {
		MulticastMessage<?> m = this.getRandomMessage();
		for (NetworkAddress addr : this.lastTo) {
			assertTrue(m.getTo().contains(addr));
		}
	}

	@Test
	public void testaddRecipient() {
		MulticastMessage<?> m = this.getRandomMessage();
		final NetworkAddress addrCheck = this.randomAddress();
		assertFalse(m.getTo().contains(addrCheck));

		m.addRecipient(addrCheck);
		assertTrue(m.getTo().contains(addrCheck));
	}

	@Test
	public void testAddRecipients() {
		MulticastMessage<?> m = this.getRandomMessage();
		final List<NetworkAddress> addrs = new ArrayList<NetworkAddress>();
		for (int i = 0; i < rand.nextInt(10); i++) {
			NetworkAddress ad = this.randomAddress();
			addrs.add(ad);
			assertFalse(m.getTo().contains(ad));
		}
		m.addRecipients(addrs);
		assertTrue(m.getTo().containsAll(addrs));
	}

}
