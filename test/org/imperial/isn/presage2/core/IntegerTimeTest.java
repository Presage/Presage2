/**
 * 
 */
package org.imperial.isn.presage2.core;

import static org.junit.Assert.*;

import org.imperial.isn.presage2.core.IntegerTime;
import org.imperial.isn.presage2.core.Time;
import org.junit.Test;

/**
 * @author Sam Macbeth
 *
 */
public class IntegerTimeTest {

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.IntegerTime#IntegerTime(int)}.
	 */
	@Test
	public void testIntegerTime() {
		IntegerTime t = new IntegerTime();
		assertNotNull(t);
		assertTrue(t instanceof IntegerTime);
		
		IntegerTime t2 = new IntegerTime(5);
		assertNotNull(t2);
		assertTrue(t2 instanceof IntegerTime);
	}

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.IntegerTime#equals(org.imperial.isn.presage2.core.Time)}.
	 */
	@Test
	public void testEqualsTime() {
		Time t1 = new IntegerTime();
		assertFalse(t1.equals(null));
		assertFalse(t1.equals(2));
		assertTrue(t1.equals(0));
		assertTrue(t1.equals(t1));
		
		Time t2 = new IntegerTime();
		assertTrue(t1.equals(t2));
		
		Time t3 = new IntegerTime(2);
		assertFalse(t2.equals(t3));
		assertFalse(t3.equals(t2));
		
	}

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.IntegerTime#increment()}.
	 */
	@Test
	public void testIncrement() {
		Time t1 = new IntegerTime(2);
		Time t2 = new IntegerTime(2);
		Time t3 = new IntegerTime(3);
		assertTrue(t1.equals(t2));
		assertFalse(t2.equals(t3));
		assertFalse(t1.equals(t3));
		
		t2.increment();
		assertFalse(t1.equals(t2));
		assertTrue(t2.equals(t3));
		assertFalse(t1.equals(t3));
	}

	/**
	 * Test method for {@link org.imperial.isn.presage2.core.IntegerTime#setTime(org.imperial.isn.presage2.core.Time)}.
	 */
	@Test
	public void testSetTime() {
		Time t1 = new IntegerTime(2);
		Time t2 = new IntegerTime(4);
		assertFalse(t1.equals(t2));
		
		t1.setTime(t2);
		assertTrue(t1.equals(t2));
		
		t1.setTime(null);
		assertTrue(t1.equals(t2));
	}
	
	/**
	 * Test method for {@link org.imperial.isn.presage2.core.IntegerTime#toString()}.
	 */
	@Test
	public void testToString() {
		Integer n = new Integer(2);
		Time t1 = new IntegerTime(n);
		assertEquals(t1.toString(), n.toString());
	}

}
