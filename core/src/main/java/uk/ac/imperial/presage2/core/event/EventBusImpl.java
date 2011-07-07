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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

/**
 * <p>An implementation of an {@link EventBus} using the {@link EventListener} annotations
 * to detect appropriate methods to subscribe.</p>
 * 
 * <p>Derived from code at http://www.mechanicalspirit.com/java-programming-tip-building-your-own-event</p>
 * 
 * @author Sam Macbeth
 *
 */
@Singleton
class EventBusImpl implements EventBus {

	private final Logger logger = Logger.getLogger(EventBusImpl.class);
	
	private Map<Class<?>, Set<WeakReference<Object>>> listeners = new HashMap<Class<?>, Set<WeakReference<Object>>>();
	
	EventBusImpl() {
		super();
	}
	
	@Override
	public synchronized void subscribe(final Object listener) {
		
		if(logger.isDebugEnabled())
			logger.debug(listener +" subscribing to eventbus");
		
		// find all @EventListeners
		Set<Method> eventListeners = new HashSet<Method>();
		for(Method method : listener.getClass().getMethods()) {
			if(method.isAnnotationPresent(EventListener.class)) {
				eventListeners.add(method);
			}
		}
		
		// examine each @EventListener
		for(Method method : eventListeners) {
			
			Class<?>[] paramTypes = method.getParameterTypes();
			
			// Disqualify malformed candidates
			if((paramTypes.length == 1) && Event.class.isAssignableFrom(paramTypes[0])) {
				addTypeSpecificListener(listener, paramTypes[0]);
			}
		}
	}
	
	@Override
	public synchronized void unsubscribe(Object listener) {
		if(logger.isDebugEnabled())
			logger.debug(listener +" unsubscribing from eventbus");
		
		for (Entry<Class<?>, Set<WeakReference<Object>>> clazzEntry : listeners.entrySet()) {
			Collection<WeakReference<Object>> toRemove = new LinkedList<WeakReference<Object>>();
			for (WeakReference<Object> listenerRef : clazzEntry.getValue()) {
				if(listenerRef.get().equals(listener)) {
					toRemove.add(listenerRef);
				}
			}
			clazzEntry.getValue().removeAll(toRemove);
		}
	}

	@Override
	public <S> void publish(final Event<S> event) {
		
		if(logger.isDebugEnabled())
			logger.debug("Taking publication of event "+event);
		
		// loop this event type and it's supertypes.
		for (Iterator<Class<?>> iter = superclassIterator(event.getClass()); iter.hasNext();) {
			
			Class<?> type = iter.next();
			// skip Object
			if (type.equals(Object.class)) {
                continue;
            }
			
			// get listeners for this type
			Set<WeakReference<Object>> typeListeners = listeners.get(type);
			if (typeListeners != null) {
				Collection<WeakReference<Object>> deadRefs = new LinkedList<WeakReference<Object>>();
				
				for (WeakReference<Object> listenerRef : typeListeners) {
					Object listener = listenerRef.get();
					if (listener != null) {
						
						// invoke methods
						for(Method m : getEventListenersFor(listener, type)) {
							try {
								if(logger.isDebugEnabled())
									logger.debug("Invoking EventListener "+listener+" with event "+event);
								m.invoke(listener, event);
							} catch (IllegalAccessException e) {
								logger.warn("Exception when invoking EventListener "+ listener +" with event "+ event, e);
							} catch (InvocationTargetException e) {
								logger.warn("Exception when invoking EventListener "+ listener +" with event "+ event, e);
							}
						}
						
					} else {
						deadRefs.add(listenerRef);
					}
				}
				synchronized (typeListeners) {
					typeListeners.removeAll(deadRefs);
				}
			}
		}
	}

	private void addTypeSpecificListener(final Object listener, final Class<?> type) {
		if(logger.isDebugEnabled())
			logger.debug("Added listener in "+listener+" for type "+type);
		// Get or create the Set of listeners for this type
        Set<WeakReference<Object>> typeListeners = listeners.get(type);
        if (typeListeners == null) {
            typeListeners = new HashSet<WeakReference<Object>>();
            listeners.put(type, typeListeners);
        }

        // Add the listener
        typeListeners.add(new WeakReference<Object>(listener));
	}
	
	private Iterator<Class<?>> superclassIterator(final Class<?> clazz) {
		
		Set<Class<?>> set = new LinkedHashSet<Class<?>>();
		
		Class<?> superClass = clazz;
		while (superClass != null) {
            set.add(superClass);
            superClass = superClass.getSuperclass();
        }
		
		return set.iterator();
	}
	
	private Set<Method> getEventListenersFor(final Object listener, final Class<?> clazz) {
		Set<Method> set = new LinkedHashSet<Method>();
		for(Method m : listener.getClass().getMethods()) {
			if(m.isAnnotationPresent(EventListener.class)) {
				Class<?>[] paramTypes = m.getParameterTypes();
				if(paramTypes[0] == clazz) {
					set.add(m);
				}
			}
		}
		return set;
	}

}
