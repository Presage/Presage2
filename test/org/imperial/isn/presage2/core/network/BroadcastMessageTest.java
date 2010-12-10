/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * @author Sam Macbeth
 *
 */
public class BroadcastMessageTest extends MessageTest {

	/**
	 * @see org.imperial.isn.presage2.core.network.MessageTest#getRandomMessage()
	 */
	@Override
	protected BroadcastMessage getRandomMessage() {
		this.lastTime = this.randomTime();
		this.lastFrom = this.randomAddress();
		this.lastPerf = this.randomPerformative();
		return new BroadcastMessage(lastPerf, lastFrom, lastTime);
	}
	
	@Override
	@Test
	public void testMessage() {
		Message m = this.getRandomMessage();
		
		assertNotNull(m);
		assertTrue(m instanceof BroadcastMessage);
	}

}
