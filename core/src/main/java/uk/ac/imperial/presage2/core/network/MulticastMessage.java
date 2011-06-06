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

import java.util.ArrayList;
import java.util.List;

import uk.ac.imperial.presage2.core.Time;
import uk.ac.imperial.presage2.core.messaging.Performative;

/**
 * <p>This is a basic multicast message to be sent between agents. </p>
 * 
 * <p>This message will only send a performative. If you want to
 * send any objects/data with the message you should extend
 * this class.</p>
 * @author Sam Macbeth
 *
 */
public class MulticastMessage extends Message {

	protected List<NetworkAddress> to;

	/**
	 * <p>Create a MulticastMessage with empty recipients list.</p>
	 * @param performative
	 * @param from
	 * @param timestamp
	 */
	public MulticastMessage(Performative performative, NetworkAddress from, Time timestamp) {
		super(performative, from, timestamp);
		this.to = new ArrayList<NetworkAddress>();
	}

	/**
	 * Create a MulticastMessage with provided list of recipients.
	 * @param performative
	 * @param from
	 * @param to
	 * @param timestamp
	 */
	public MulticastMessage(Performative performative, NetworkAddress from, List<NetworkAddress> to, 
			Time timestamp) {
		super(performative, from, timestamp);
		this.to = to;
	}
	
	/**
	 * Add a single recipient to the list.
	 * @param recipient
	 */
	public void addRecipient(NetworkAddress recipient) {
		this.to.add(recipient);
	}
	
	public void addRecipients(List<NetworkAddress> recipients) {
		this.to.addAll(recipients);
	}
	
	public List<NetworkAddress> getTo() {
		return this.to;
	}

	/**
	 * @see uk.ac.imperial.presage2.core.network.Message#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() +": (Time: "+this.timestamp.toString()+", from: "+this.from.toString()+", to: "+this.to.size()+" recipients, perf: "+this.performative.toString() +")";
	}
	
}
