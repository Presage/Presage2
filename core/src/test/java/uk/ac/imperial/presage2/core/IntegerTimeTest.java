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
package uk.ac.imperial.presage2.core;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.imperial.presage2.core.IntegerTime;
import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.util.random.Random;

/**
 * @author Sam Macbeth
 *
 */
public class IntegerTimeTest {

	final Random rand = Random.getInstance();
	
	/**
	 * Test method for {@link uk.ac.imperial.presage2.core.IntegerTime#IntegerTime(int)}.
	 */
	@Test
	public void testIntegerTime() {
		IntegerTime t = new IntegerTime();
		assertNotNull(t);
		assertTrue(t instanceof IntegerTime);
		
		IntegerTime t2 = new IntegerTime(rand.nextInt());
		assertNotNull(t2);
		assertTrue(t2 instanceof IntegerTime);
	}

	/**
	 * Test method for {@link uk.ac.imperial.presage2.core.IntegerTime#equals(uk.ac.imperial.presage2.core.Time)}.
	 */
	@Test
	public void testEqualsTime() {
		final int defaultValue = 0; // value given by default constructor
		final int t3Value = rand.nextInt();
		
		// default constructor vs itself and literals + null
		Time t1 = new IntegerTime();
		
		// check assert equals vs null is false
		assertFalse("IntegerTime(0).equals(null) should be false", t1.equals(null));
		// build random check value that != defaultValue
		int t1Check;
		do {
			t1Check = rand.nextInt();
		} while(t1Check == defaultValue);
		assertFalse("IntegerTime().equals(x != 0) should be false", t1.equals(t1Check));
		
		assertTrue("IntegerTime().equals(0) should be true", t1.equals(defaultValue));
		
		assertTrue("A.equals(A) should be true", t1.equals(t1));
		
		// default constructor vs copy
		Time t2 = new IntegerTime();
		assertTrue("IntegerTime().equals(IntegerTime()) should be true", t1.equals(t2));
		
		// int constructor vs other values and literals.
		Time t3 = new IntegerTime(t3Value);
		assertFalse("IntegerTime().equals(IntegerTime(N != 0)) should be false", t2.equals(t3));
		assertFalse("IntegerTime(N != 0).equals(IntegerTime()) should be false", t3.equals(t2));
		assertTrue("IntegerTime(N).equals(N) should be true", t3.equals(t3Value));
		assertTrue("IntegerTime(N).equals(Integer(N)) should be true", t3.equals(new Integer(t3Value)));
		// build random check value that != t3Value
		int t3Check;
		do {
			t3Check = rand.nextInt();
		} while(t3Check == t3Value);
		assertFalse("IntegerTime(N).equals(X != N) should be false", t3.equals(t3Check));
		assertFalse("IntegerTime(N).equals(Integer(X != N)) should be false", t3.equals(new Integer(t3Check)));
		
		assertTrue("IntegerTime.equals should work with Time cast to object", t1.equals((Object) t2));
		assertFalse("IntegerTime.equals should work with Time cast to object", t2.equals((Object) t3));
		
		assertFalse("IntegerTime.equals(A) where A is not Time or Integer should return false", t1.equals(new String("0")));
		
	}

	/**
	 * Test method for {@link uk.ac.imperial.presage2.core.IntegerTime#increment()}.
	 */
	@Test
	public void testIncrement() {
		final int initialTime = rand.nextInt();
		Time t1 = new IntegerTime(initialTime);
		Time t2 = new IntegerTime(initialTime);
		Time t3 = new IntegerTime(initialTime+1);
		assertTrue(t1.equals(t2));
		assertFalse(t2.equals(t3));
		assertFalse(t1.equals(t3));
		
		t2.increment();
		assertFalse(t1.equals(t2));
		assertTrue(t2.equals(t3));
		assertFalse(t1.equals(t3));
	}

	/**
	 * Test method for {@link uk.ac.imperial.presage2.core.IntegerTime#setTime(uk.ac.imperial.presage2.core.Time)}.
	 */
	@Test
	public void testSetTime() {
		Time t1 = new IntegerTime(rand.nextInt());
		Time t2 = new IntegerTime(rand.nextInt());
		assertFalse(t1.equals(t2));
		
		t1.setTime(t2);
		assertTrue(t1.equals(t2));
		
		t1.setTime(null);
		assertTrue(t1.equals(t2));
	}
	
	/**
	 * Test method for {@link uk.ac.imperial.presage2.core.IntegerTime#toString()}.
	 */
	@Test
	public void testToString() {
		Integer n = new Integer(rand.nextInt());
		Time t1 = new IntegerTime(n);
		assertEquals(t1.toString(), n.toString());
	}

	@Test
	public void testClone() {
		Time t1 = new IntegerTime(rand.nextInt());
		Time t2 = t1.clone();
		
		assertTrue("equals of cloned IntegerTime should be true", t1.equals(t2));
		assertNotSame("Cloned objects should not reference the same object", t2, t1);
	}
	
	@Test
	public void testGreaterThan() {
		// build test values
		final int lower = rand.nextInt(Integer.MAX_VALUE-1);
		final int higher = rand.nextInt(Integer.MAX_VALUE-lower)+lower;
		assertTrue("Did not generate valid values", lower < higher);
		
		Time t1 = new IntegerTime(lower);
		Time t2 = new IntegerTime(lower);
		Time t3 = new IntegerTime(higher);
		
		assertTrue("IntegerTime(Y>X).greaterThan(X) should be true", t3.greaterThan(t1));
		assertFalse("IntegerTime(Y<X).greaterThan(X) should be false", t1.greaterThan(t3));
		assertFalse("IntegerTime(Y==X).greaterThan(X) should be false", t1.greaterThan(t2));
		assertFalse("A.greaterThan(A) should be false", t1.greaterThan(t1));
		
		assertFalse("A.greaterThan(null) should be false", t1.greaterThan(null));

	}
	
}
