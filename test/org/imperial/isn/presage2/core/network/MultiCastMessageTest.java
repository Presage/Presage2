/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Sam Macbeth
 *
 */
public class MultiCastMessageTest extends MessageTest {

	protected List<NetworkAddress> lastTo;
	
	/**
	 * @see org.imperial.isn.presage2.core.network.MessageTest#getRandomMessage()
	 */
	@Override
	protected MulticastMessage getRandomMessage() {
		this.lastTime = this.randomTime();
		this.lastFrom = this.randomAddress();
		this.lastTo = new ArrayList<NetworkAddress>();
		for(int i=0; i<rand.nextInt(10); i++) {
			this.lastTo.add(this.randomAddress());
		}
		this.lastPerf = this.randomPerformative();
		return new MulticastMessage(lastPerf, lastFrom, lastTo, lastTime);
	}

	/**
	 * @see org.imperial.isn.presage2.core.network.MessageTest#testMessage()
	 */
	@Override
	@Test
	public void testMessage() {
		Message m = this.getRandomMessage();
		
		assertNotNull(m);
		assertTrue(m instanceof MulticastMessage);
		
		// test constructor with no to args
		MulticastMessage m2 = new MulticastMessage(lastPerf, lastFrom, lastTime);
		
		assertNotNull(m2);
		assertTrue(m2 instanceof MulticastMessage);
		
		assertTrue(m2.to.isEmpty());
	}
	
	@Test
	public void testGetTo() {
		MulticastMessage m = this.getRandomMessage();
		for(NetworkAddress addr : this.lastTo) {
			assertTrue(m.getTo().contains(addr));
		}
	}
	
	@Test
	public void testaddRecipient() {
		MulticastMessage m = this.getRandomMessage();
		final NetworkAddress addrCheck = this.randomAddress();
		assertFalse(m.getTo().contains(addrCheck));
		
		m.addRecipient(addrCheck);
		assertTrue(m.getTo().contains(addrCheck));
	}
	
	@Test
	public void testAddRecipients() {
		MulticastMessage m = this.getRandomMessage();
		final List<NetworkAddress> addrs = new ArrayList<NetworkAddress>();
		for(int i=0; i<rand.nextInt(10); i++) {
			NetworkAddress ad = this.randomAddress();
			addrs.add(ad);
			assertFalse(m.getTo().contains(ad));
		}
		m.addRecipients(addrs);
		assertTrue(m.getTo().containsAll(addrs));
	}

}
