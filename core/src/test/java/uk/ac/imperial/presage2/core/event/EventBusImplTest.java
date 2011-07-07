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
package uk.ac.imperial.presage2.core.event;

import static org.junit.Assert.*;

import org.junit.Test;

import org.jmock.Mockery;

import uk.ac.imperial.presage2.core.Time;

public class EventBusImplTest {

	class MockEvent implements Event<Object> {
		@Override
		public Object getSource() {
			return null;
		}
		@Override
		public Time getTime() {
			return null;
		}
	}
	
	private int invocationCount = 0;
	
	class MockEventListener {
		
		@EventListener
		public void hearMockEvent(MockEvent e) {
			invocationCount++;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEventBusImpl() {
		
		Mockery context = new Mockery();
		Event fakeEvent = context.mock(Event.class);
		MockEventListener listener = new MockEventListener();
		
		EventBus eventBus = new EventBusImpl();
		
		// assert no invocation before subscription.
		eventBus.publish(new MockEvent());
		assertTrue(invocationCount == 0);
		
		// assert invocation after subscription
		eventBus.subscribe(listener);
		eventBus.publish(new MockEvent());
		assertTrue(invocationCount == 1);
		
		// assert no invocation from different event
		eventBus.publish(fakeEvent);
		assertTrue(invocationCount == 1);
		
		// assert no invocation after unsubscribe
		eventBus.unsubscribe(listener);
		eventBus.publish(new MockEvent());
		assertTrue(invocationCount == 1);
	}
	
}
