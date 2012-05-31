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
package uk.ac.imperial.presage2.util.location;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.imperial.presage2.core.util.random.Random;

/**
 * @author Sam Macbeth
 *
 */
public class TestLocation2D {

	/**
	 * Test creation of a {@link Continuous2DLocation}
	 */
	@Test
	public void testContinuousLocation2D() {
		final double x = Random.randomDouble();
		final double y = Random.randomDouble();
		Location l = new Location(x, y);
		assertNotNull(l);
		assertEquals(x, l.getX(), 0);
		assertEquals(y, l.getY(), 0);

		assertSame(l, l.getLocation());
	}

	@Test
	public void testDiscreteLocation2D() {
		final int x = Random.randomInt();
		final int y = Random.randomInt();
		Location l = new Location(x, y);
		assertNotNull(l);
		assertTrue(x == l.getX());
		assertTrue(y == l.getY());

		assertSame(l, l.getLocation());
	}

	@Test
	public void testEquals() {

		// create actual locations
		final double x1 = Random.randomDouble();
		final double y1 = Random.randomDouble();
		Location l1 = new Location(x1, y1);

		final int x2 = Random.randomInt();
		final int y2 = Random.randomInt();
		Location l2 = new Location(x2, y2);

		// test null comparisons
		assertFalse(l1.equals(null));
		assertFalse(l2.equals(null));

		// test same object comparison
		assertTrue(l1.equals(l1));
		assertTrue(l2.equals(l2));

		// test same object comparison with Location cast
		assertTrue(l1.equals(l1));
		assertTrue(l2.equals(l2));
		assertTrue((l1).equals(l1));
		assertTrue((l2).equals(l2));
		assertTrue((l1).equals(l1));
		assertTrue((l2).equals(l2));

		// test equal but different object
		assertTrue(l1.equals(new Location(x1, y1)));
		assertTrue(l1.equals(new Location(x1, y1)));
		assertTrue(l2.equals(new Location(x2, y2)));
		assertTrue(l2.equals(new Location(x2, y2)));
		assertTrue(new Location(x1, y1).equals(l1));
		assertTrue(new Location(x2, y2).equals(l2));

		// test same type but not equal
		assertFalse(l1.equals(new Location(Random.randomDouble(), Random.randomDouble())));
		assertFalse(new Location(Random.randomDouble(), Random.randomDouble()).equals(l1));
		assertFalse(l2.equals(new Location(Random.randomInt(), Random.randomInt())));
		assertFalse(new Location(Random.randomInt(), Random.randomInt()).equals(l2));

		// test l1 - l2 comparison
		assertFalse(l1.equals(l2));
		assertFalse(l2.equals(l1));
	}

	@Test
	public void testContinuousDistanceTo() {
		final double x1 = Random.randomDouble();
		final double y1 = Random.randomDouble();
		final Location l1 = new Location(x1, y1);

		final double x2 = Random.randomDouble();
		final double y2 = Random.randomDouble();
		final Location l2 = new Location(x2, y2);

		final double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

		assertEquals(0, l1.distanceTo(l1), 0);
		assertEquals(0, l2.distanceTo(l2), 0);

		assertEquals(distance, l1.distanceTo(l2), 0);
		assertEquals(distance, l2.distanceTo(l1), 0);

		assertEquals(distance, l1.distanceTo(l2), 0);
		assertEquals(distance, l2.distanceTo(l1), 0);

	}

	@Test
	public void testDiscreteDistanceTo() {
		final int x1 = Random.randomInt(100);
		final int y1 = Random.randomInt(100);
		final Location l1 = new Location(x1, y1);

		final int x2 = Random.randomInt(100);
		final int y2 = Random.randomInt(100);
		final Location l2 = new Location(x2, y2);

		final double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

		assertEquals(0, l1.distanceTo(l1), 0);
		assertEquals(0, l2.distanceTo(l2), 0);

		assertEquals(distance, l1.distanceTo(l2), 0);
		assertEquals(distance, l2.distanceTo(l1), 0);

		assertEquals(distance, l1.distanceTo(l2), 0);
		assertEquals(distance, l2.distanceTo(l1), 0);

	}

	@Test
	public void testDiscreteGetMoveTo() {
		final int x = Random.randomInt();
		final int y = Random.randomInt();
		final Location l1 = new Location(x, y);
		final int dx = Random.randomInt(10) - 5;
		final int dy = Random.randomInt(10) - 5;
		final Location l2 = new Location(x + dx, y + dy);

		final Move m = l1.getMoveTo(l2);
		assertEquals(m.getX(), dx, 0);
		assertEquals(m.getY(), dy, 0);

	}

	@Test
	public void testContinuousGetMoveTo() {
		final double x = Random.randomDouble() * Random.randomInt();
		final double y = Random.randomDouble() * Random.randomInt();
		final Location l1 = new Location(x, y);
		final double dx = Random.randomDouble() * Random.randomInt();
		final double dy = Random.randomDouble() * Random.randomInt();
		final Location l2 = new Location(x + dx, y + dy);

		final Move m = l1.getMoveTo(l2);
		assertEquals(m.getX(), dx, 0.000001);
		assertEquals(m.getY(), dy, 0.000001);

	}

	@Test
	public void testDiscreteGetMoveToWithSpeed() {
		final int x = Random.randomInt();
		final int y = Random.randomInt();
		final Location l1 = new Location(x, y);
		final int dx = Random.randomInt(10) - 5;
		final int dy = Random.randomInt(10) - 5;
		final Location l2 = new Location(x + dx, y + dy);

		final double highSpeed = Math.sqrt(dx * dx + dy * dy) + Random.randomInt(5);
		final Move m1 = l1.getMoveTo(l2, highSpeed);
		assertEquals(m1.getX(), dx, 0);
		assertEquals(m1.getY(), dy, 0);

		final double lowSpeed = Math.sqrt(dx * dx + dy * dy)
				- Random.randomInt((int) Math.max(Math.floor(Math.sqrt(dx * dx + dy * dy)), 1));
		final Move m2 = l1.getMoveTo(l2, lowSpeed);
		assertEquals(m2.getNorm(), lowSpeed, 0.0001);
		assertEquals(Location.angle(m1, m2), 0, 0);
	}

}
