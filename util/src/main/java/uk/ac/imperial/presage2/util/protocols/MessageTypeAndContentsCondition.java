/**
 * 	Copyright (C) 2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

package uk.ac.imperial.presage2.util.protocols;

import uk.ac.imperial.presage2.core.network.Message;
import uk.ac.imperial.presage2.util.fsm.State;
import uk.ac.imperial.presage2.util.fsm.TransitionCondition;

/**
 * FSM {@link TransitionCondition} to check the <code>type</code> and <code>data</code> properties of a
 * received message matches a given value.
 * @author dws04
 *
 * @param <T>
 */
public class MessageTypeAndContentsCondition<T> implements TransitionCondition {

	private final String type;
	private final T data;
	
	public MessageTypeAndContentsCondition(String type, T data) {
		super();
		this.type = type;
		this.data = data;
	}

	@Override
	public boolean allow(Object event, Object entity, State state) {
		if (event instanceof Message){
			@SuppressWarnings("unchecked")
			Message<T> m = (Message<T>) event;
			return (type.equals(m.getType()) && (m.getData().equals(data)));
		}
		return false;
	}

}
