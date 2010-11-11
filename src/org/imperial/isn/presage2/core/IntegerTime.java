/**
 * 
 */
package org.imperial.isn.presage2.core;


/**
 * @author Sam Macbeth
 *
 */
public class IntegerTime implements Time {

	private int time = 0;
	
	/**
	 * @param time
	 */
	public IntegerTime(int time) {
		super();
		this.time = time;
	}

	/**
	 * @see org.imperial.isn.presage2.core.Time#equals()
	 */
	@Override
	public boolean equals(Time t) {
		if(t instanceof IntegerTime) {
			IntegerTime it = (IntegerTime) t;
			return it.time == this.time;
		}
		return false;
	}

	/**
	 * @see org.imperial.isn.presage2.core.Time#increment()
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

}
