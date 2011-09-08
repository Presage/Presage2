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
package uk.ac.imperial.presage2.core.simulator;

import com.google.inject.Inject;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.event.EventBus;
import uk.ac.imperial.presage2.core.event.EventListener;

/**
 * Provides static access to the current simulation time.
 * 
 * @author Sam Macbeth
 * 
 */
public class SimTime {

	private static SimTime instance;

	private final Time time;
	private final ImmutableTime immutableTime;

	@Inject
	public SimTime(Time t, EventBus eb) {
		this.time = t;
		this.immutableTime = new ImmutableTime(this.time);
		eb.subscribe(this);
		SimTime.instance = this;
	}

	@EventListener
	public void incrementTime(EndOfTimeCycle event) {
		this.time.increment();
	}

	public static Time get() {
		return SimTime.instance.getTime();
	}

	Time getTime() {
		return this.immutableTime;
	}

	private class ImmutableTime implements Time {

		private Time delegate;

		ImmutableTime(Time delegate) {
			this.delegate = delegate;
		}

		@Override
		public String toString() {
			return delegate.toString();
		}

		@Override
		public boolean equals(Time t) {
			return delegate.equals(t);
		}

		@Override
		public void increment() {
			// do nothing
		}

		@Override
		public void setTime(Time t) {
			// do nothing
		}

		@Override
		public Time clone() {
			return this;
		}

		@Override
		public boolean greaterThan(Time t) {
			return delegate.greaterThan(t);
		}

		@Override
		public int intValue() {
			return delegate.intValue();
		}

	}

}
