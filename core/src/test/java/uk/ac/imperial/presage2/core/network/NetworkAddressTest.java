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

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.network.NetworkAddressFactory;
import uk.ac.imperial.presage2.core.network.NetworkGuiceModule;
import uk.ac.imperial.presage2.core.util.random.Random;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Sam Macbeth
 * 
 */
public class NetworkAddressTest {

	final private Random rand = Random.getInstance();

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
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.NetworkAddress#NetworkAddress(java.util.UUID)}
	 * .
	 */
	@Test
	public void testNetworkAddress() {
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());
		final NetworkAddress addr = new NetworkAddress(id);

		assertNotNull(addr);

		// we should not be allowed to pass null to a networkaddress.
		try {
			@SuppressWarnings("unused")
			final NetworkAddress nullAddr = new NetworkAddress(null);
			fail("NetworkAddress allowed to instaniate with null ID, NullPointerException expected.");
		} catch (NullPointerException e) {

		}

	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.NetworkAddress#getId()}.
	 */
	@Test
	public void testGetId() {
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());
		final NetworkAddress testAddr = new NetworkAddress(id);
		assertEquals(id, testAddr.getId());
	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.NetworkAddress#toString()}.
	 */
	@Test
	public void testToString() {
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());
		final NetworkAddress testAddr = new NetworkAddress(id);
		assertNotSame("", testAddr.toString());
	}

	/**
	 * Test the guice generated NetworkAddressFactory
	 */
	@Test
	public void testNetworkAddressFactory() {
		// create injector
		Injector injector = Guice.createInjector(new NetworkGuiceModule());
		// create factory
		NetworkAddressFactory factory = injector
				.getInstance(NetworkAddressFactory.class);

		// create UUID for address
		final UUID id = new UUID(rand.nextLong(), rand.nextLong());

		// attempt to create address
		final NetworkAddress generatedAddr = factory.create(id);

		assertEquals(id, generatedAddr.getId());
	}

}
