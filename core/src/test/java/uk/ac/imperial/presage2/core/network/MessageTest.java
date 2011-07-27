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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import uk.ac.imperial.presage2.core.IntegerTime;
import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.messaging.Performative;
import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.core.network.NetworkAddress;
import uk.ac.imperial.presage2.core.util.random.Random;

public abstract class MessageTest {

	protected final Random rand = Random.getInstance();

	protected Time lastTime;

	protected NetworkAddress lastFrom;

	protected Performative lastPerf;

	public MessageTest() {
		super();
	}

	/**
	 * <p>
	 * Generate a random message to test
	 * </p>
	 * <p>
	 * This method should set lastTime, lastFrom and lastPerf fields to allow
	 * testing of expected outcomes.
	 * </p>
	 * 
	 * @return
	 */
	protected abstract Message<?> getRandomMessage();

	/**
	 * Generates a random instance of time for instantiating in a test message
	 * 
	 * @return random Time
	 */
	protected Time randomTime() {
		return new IntegerTime(rand.nextInt());
	}

	/**
	 * Generates a random instance of NetworkAddress for instantiating in a test
	 * message
	 * 
	 * @return random NetworkAddress
	 */
	protected NetworkAddress randomAddress() {
		return new NetworkAddress(new UUID(rand.nextLong(), rand.nextLong()));
	}

	/**
	 * Generates a random Performative for instantiating in a test message
	 * 
	 * @return random Performative
	 */
	protected Performative randomPerformative() {
		return Performative.values()[rand
				.nextInt(Performative.values().length - 1)];
	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.Message#toString()}.
	 */
	@Test
	public void testToString() {
		// ensure tostring is not empty string
		Message<?> m = this.getRandomMessage();
		assertNotNull(m.toString());
		assertFalse(m.toString().equals(""));
	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.Message#Message(uk.ac.imperial.presage2.core.messaging.Performative, uk.ac.imperial.presage2.core.network.NetworkAddress, uk.ac.imperial.presage2.core.Time)}
	 * .
	 */
	@Test
	public void testMessage() {
		fail("Must be implemented for Message implementation.");
	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.Message#getPerformative()}.
	 */
	@Test
	public void testGetPerformative() {
		Message<?> m = this.getRandomMessage();
		assertEquals(this.lastPerf, m.getPerformative());

	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.Message#getTimestamp()}.
	 */
	@Test
	public void testGetTimestamp() {
		Message<?> m = this.getRandomMessage();
		assertEquals(this.lastTime, m.getTimestamp());
	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.Message#setTimestamp(uk.ac.imperial.presage2.core.Time)}
	 * .
	 */
	@Test
	public void testSetTimestamp() {
		Message<?> m = this.getRandomMessage();
		assertEquals(this.lastTime, m.getTimestamp());
		final Time timeTest = new IntegerTime(rand.nextInt());
		m.setTimestamp(timeTest);
		assertEquals(timeTest, m.getTimestamp());
	}

	/**
	 * Test method for
	 * {@link uk.ac.imperial.presage2.core.network.Message#getFrom()}.
	 */
	@Test
	public void testGetFrom() {
		Message<?> m = this.getRandomMessage();
		assertEquals(this.lastFrom, m.getFrom());
	}

}