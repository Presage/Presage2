/**
 * 
 */
package org.imperial.isn.presage2.core.network;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sam Macbeth
 *
 */
public class NetworkAddressTest {

	final private Random rand = new Random();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.network.NetworkAddress#NetworkAddress(java.util.UUID)}.
	 */
	@Test
	public void testNetworkAddress() {
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());
		final NetworkAddress addr = new NetworkAddress(id);
		
		assertNotNull(addr);
		assertTrue(addr instanceof NetworkAddress);
		
		// we should not be allowed to pass null to a networkaddress.
		try {
			final NetworkAddress nullAddr = new NetworkAddress(null);
			fail("NetworkAddress allowed to instaniate with null ID, NullPointerException expected.");
		} catch(NullPointerException e) {
			
		}
		
	}

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.network.NetworkAddress#getId()}.
	 */
	@Test
	public void testGetId() {
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());
		final NetworkAddress testAddr = new NetworkAddress(id);
		assertEquals(id, testAddr.getId());
	}

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.network.NetworkAddress#toString()}.
	 */
	@Test
	public void testToString() {
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());
		final NetworkAddress testAddr = new NetworkAddress(id);
		assertNotSame(new String(""), testAddr.toString());
	}

}
