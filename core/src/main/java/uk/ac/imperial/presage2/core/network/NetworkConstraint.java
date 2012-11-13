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
package uk.ac.imperial.presage2.core.network;

/**
 * @author Sam Macbeth
 * 
 */
public interface NetworkConstraint {

	/**
	 * Apply constraints to the {@link Message} m and return the modified
	 * version of the message.
	 * 
	 * @param m
	 * @return
	 */
	public Message constrainMessage(Message m);

	/**
	 * Block the message m from being delivered to recipient to.
	 * 
	 * @param to
	 *            a recipient of the message m
	 * @param m
	 *            the message being sent.
	 * @return True if the NetworkController should block the message, false
	 *         otherwise.
	 */
	public boolean blockMessageDelivery(NetworkAddress to, Message m);

}
