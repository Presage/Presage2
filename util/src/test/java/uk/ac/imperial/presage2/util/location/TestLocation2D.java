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

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.imperial.presage2.core.util.random.Random;

/**
 * @author Sam Macbeth
 *
 */
public class TestLocation2D {

	// set up mock Location
	final Location testLoc = new Location() {
		@Override
		public Location getLocation() {
			return this;
		}
		@Override
		public String toString() {
			return null;
		}
		@Override
		public boolean equals(Location l) {
			return false;
		}
		@Override
		public double distanceTo(Location l) {
			return 0;
		}
		@Override
		public Location add(Move m) {
			return this;
		}
	};
	
	/**
	 * Test creation of a {@link Continuous2DLocation}
	 */
	@Test
	public void testContinuousLocation2D() {
		final double x = Random.randomDouble();
		final double y = Random.randomDouble();
		Continuous2DLocation l = new Continuous2DLocation(x, y);
		assertNotNull(l);
		assertEquals(x, l.x, 0);
		assertEquals(y, l.y, 0);
		
		assertSame(l, l.getLocation());
	}
	
	@Test
	public void testDiscreteLocation2D() {
		final int x = Random.randomInt();
		final int y = Random.randomInt();
		Discrete2DLocation l = new Discrete2DLocation(x, y);
		assertNotNull(l);
		assertTrue(x == l.x);
		assertTrue(y == l.y);
		
		assertSame(l, l.getLocation());
	}
	
	@Test
	public void testToString() {
		// continuous
		final double x = Random.randomDouble();
		final double y = Random.randomDouble();
		Continuous2DLocation l = new Continuous2DLocation(x, y);
		assertEquals("("+x+","+y+")", l.toString());
		
		// discrete
		final int x2 = Random.randomInt();
		final int y2 = Random.randomInt();
		Discrete2DLocation l2 = new Discrete2DLocation(x2, y2);
		assertEquals("("+x2+","+y2+")", l2.toString());
	}
	
	@Test
	public void testEquals() {
		
		// create actual locations
		final double x1 = Random.randomDouble();
		final double y1 = Random.randomDouble();
		Continuous2DLocation l1 = new Continuous2DLocation(x1, y1);
		
		final int x2 = Random.randomInt();
		final int y2 = Random.randomInt();
		Discrete2DLocation l2 = new Discrete2DLocation(x2, y2);
		
		// test null comparisons
		assertFalse(l1.equals(null));
		assertFalse(l2.equals(null));
		
		// test non Location2D comparison
		assertFalse(l1.equals(testLoc));
		assertFalse(l2.equals(testLoc));
		
		// test same object comparison
		assertTrue(l1.equals(l1));
		assertTrue(l2.equals(l2));
		
		// test same object comparison with Location cast
		assertTrue(l1.equals((Location) l1));
		assertTrue(l2.equals((Location) l2));
		assertTrue(((Location) l1).equals((Location) l1));
		assertTrue(((Location) l2).equals((Location) l2));
		assertTrue(((Location) l1).equals(l1));
		assertTrue(((Location) l2).equals(l2));
		
		// test equal but different object
		assertTrue(l1.equals(new Continuous2DLocation(x1, y1)));
		assertTrue(l1.equals((Location) new Continuous2DLocation(x1, y1)));
		assertTrue(l2.equals(new Discrete2DLocation(x2, y2)));
		assertTrue(l2.equals((Location) new Discrete2DLocation(x2, y2)));
		assertTrue(new Continuous2DLocation(x1, y1).equals(l1));
		assertTrue(new Discrete2DLocation(x2, y2).equals(l2));
		
		// test same type but not equal
		assertFalse(l1.equals(new Continuous2DLocation(Random.randomDouble(), Random.randomDouble())));
		assertFalse(new Continuous2DLocation(Random.randomDouble(), Random.randomDouble()).equals(l1));
		assertFalse(l2.equals(new Discrete2DLocation(Random.randomInt(), Random.randomInt())));
		assertFalse(new Discrete2DLocation(Random.randomInt(), Random.randomInt()).equals(l2));
		
		// test l1 - l2 comparison
		assertFalse(l1.equals(l2));
		assertFalse(l2.equals(l1));
	}
	
	@Test
	public void testContinuousDistanceTo() {
		final double x1 = Random.randomDouble();
		final double y1 = Random.randomDouble();
		final Continuous2DLocation l1 = new Continuous2DLocation(x1, y1);
		
		final double x2 = Random.randomDouble();
		final double y2 = Random.randomDouble();
		final Continuous2DLocation l2 = new Continuous2DLocation(x2, y2);
		
		final double distance = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		
		assertEquals(0, l1.distanceTo(l1), 0);
		assertEquals(0, l2.distanceTo(l2), 0);
		
		assertEquals(distance, l1.distanceTo(l2), 0);
		assertEquals(distance, l2.distanceTo(l1), 0);
		
		assertEquals(distance, l1.distanceTo((Location) l2), 0);
		assertEquals(distance, l2.distanceTo((Location) l1), 0);
		
		try {
			l1.distanceTo(testLoc);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			l2.distanceTo(testLoc);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			l1.distanceTo(new Location2D<Number>() {
				@Override
				public Location add(Move m) {
					return this;
				}
			});
			fail();
		} catch (UnsupportedOperationException e) {}
		
	}
	
	@Test
	public void testDiscreteDistanceTo() {
		final int x1 = Random.randomInt();
		final int y1 = Random.randomInt();
		final Discrete2DLocation l1 = new Discrete2DLocation(x1, y1);
		
		final int x2 = Random.randomInt();
		final int y2 = Random.randomInt();
		final Discrete2DLocation l2 = new Discrete2DLocation(x2, y2);
		
		final double distance = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
		
		assertEquals(0, l1.distanceTo(l1), 0);
		assertEquals(0, l2.distanceTo(l2), 0);
		
		assertEquals(distance, l1.distanceTo(l2), 0);
		assertEquals(distance, l2.distanceTo(l1), 0);
		
		assertEquals(distance, l1.distanceTo((Location) l2), 0);
		assertEquals(distance, l2.distanceTo((Location) l1), 0);
		
		try {
			l1.distanceTo(testLoc);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			l2.distanceTo(testLoc);
			fail();
		} catch (UnsupportedOperationException e) {}
		try {
			l1.distanceTo(new Location2D<Number>() {
				@Override
				public Location add(Move m) {
					return this;
				}
			});
			fail();
		} catch (UnsupportedOperationException e) {}
	}
	
}
