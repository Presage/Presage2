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

package uk.ac.imperial.presage2.core;


/**
 * @author Sam Macbeth
 *
 */
public class IntegerTime implements Time {

	private int time = 0;
	
	public IntegerTime() {
		
	}
	/**
	 * @param time
	 */
	public IntegerTime(int time) {
		super();
		this.time = time;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.Time#equals()
	 */
	@Override
	public boolean equals(Time t) {
		if(t instanceof IntegerTime) {
			IntegerTime it = (IntegerTime) t;
			return it.time == this.time;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Time) {
			return this.equals((Time) o);
		} else if(o instanceof Integer) {
			return ((Integer) o).intValue() == this.time;
		} else
			return false;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.Time#increment()
	 */
	@Override
	public void increment() {
		this.time++;
	}

	@Override
	public void setTime(Time t) {
		if(t instanceof IntegerTime) {
			IntegerTime it = (IntegerTime) t;
			this.time = it.time;
		}
	}
	
	@Override
	public String toString() {
		return new Integer(this.time).toString();
	}
	
	@Override
	public Time clone() {
		return new IntegerTime(this.time);
	}
	
	@Override
	public boolean greaterThan(Time t) {
		if(t != null)
			return this.time > ((IntegerTime) t).time;
		else
			return false;
	}

}
