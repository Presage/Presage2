/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

package uk.ac.imperial.presage2.util.network;

import java.util.List;
import java.util.Set;

import uk.ac.imperial.presage2.core.environment.ActionHandlingException;
import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * <p>
 * A network adaptor is a participant's perception of it's communication channel
 * to the outside world.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public interface NetworkAdaptor {

	/**
	 * Gets inputs currently queued at this adaptor.
	 * 
	 * @return list of messages received.
	 */
	public List<Message> getMessages();

	/**
	 * Gets this device's network address
	 * 
	 * @return this device's network address
	 */
	public NetworkAddress getAddress();

	/**
	 * Sends a message through the network.
	 * 
	 * @param m
	 *            {@link Message} to send.
	 * @throws ActionHandlingException
	 *             if an exception is thrown by the underlying call to
	 *             {@link Participant#act(uk.ac.imperial.presage2.core.Action)}
	 */
	public void sendMessage(Message m) throws ActionHandlingException;

	/**
	 * The network adaptor may also provide a network node discovery service
	 * which we describe in the following form.
	 * 
	 * @return List of UUIDs of connected nodes.
	 * @throws UnsupportedOperationException
	 *             if the networkadaptor does not support this operation.
	 */
	public Set<NetworkAddress> getConnectedNodes()
			throws UnsupportedOperationException;

}
