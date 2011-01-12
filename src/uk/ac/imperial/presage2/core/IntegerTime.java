/**
 * 
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
