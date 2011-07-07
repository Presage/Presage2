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

/**
 * <p>
 * The EventBus manages the distribution of events to {@link EventListener}s who
 * listen for them. Any object may {@link #subscribe(Object)} and
 * {@link #unsubscribe(Object)} to the eventbus. Upon subscription any methods
 * annotated with {@link EventListener} and containing one argument whose type
 * implements {@link Event} will be registered. These methods will then be
 * invoked should any object call publish with an event whose type matches that
 * of the event listener's method's argument.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public interface EventBus {

	public void subscribe(final Object listener);

	public void unsubscribe(final Object listener);

	public void publish(final Event event);

}
