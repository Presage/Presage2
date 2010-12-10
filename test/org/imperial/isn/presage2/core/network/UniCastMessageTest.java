/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Sam Macbeth
 *
 */
public class UniCastMessageTest extends MessageTest {

	protected NetworkAddress lastTo;
	
	/**
	 * Test method for {@link org.imperial.isn.presage2.core.network.UnicastMessage#UnicastMessage(org.imperial.isn.presage2.core.messaging.Performative, org.imperial.isn.presage2.core.network.NetworkAddress, org.imperial.isn.presage2.core.network.NetworkAddress, org.imperial.isn.presage2.core.Time)}.
	 */
	@Test
	@Override
	public void testMessage() {
		Message m = this.getRandomMessage();
		
		assertNotNull(m);
		assertTrue(m instanceof UnicastMessage);
		
	}

	@Override
	protected UnicastMessage getRandomMessage() {
		this.lastTime = this.randomTime();
		this.lastFrom = this.randomAddress();
		this.lastTo = this.randomAddress();
		this.lastPerf = this.randomPerformative();
		return new UnicastMessage(lastPerf, lastFrom, lastTo, lastTime);
	}
	
	/**
	 * 
	 */
	@Test
	public void testGetTo() {
		UnicastMessage m = this.getRandomMessage();
		assertEquals(this.lastTo, m.getTo());
	}

}
