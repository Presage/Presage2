/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import static org.junit.Assert.*;

import java.util.UUID;

import org.imperial.isn.presage2.core.IntegerTime;
import org.imperial.isn.presage2.core.Time;
import org.imperial.isn.presage2.core.messaging.Performative;
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
		this.lastTime = new IntegerTime(rand.nextInt());
		this.lastFrom = new NetworkAddress(new UUID(rand.nextLong(), rand.nextLong()));
		this.lastTo = new NetworkAddress(new UUID(rand.nextLong(), rand.nextLong()));
		this.lastPerf = Performative.values()[rand.nextInt(Performative.values().length -1)];
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
