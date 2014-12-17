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

import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * <p>
 * An address of a device in the network.
 * </p>
 * 
 * <p>
 * Using this we can send messages to this device
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class NetworkAddress {

	/**
	 * Unique UUID of device we're sending the message to. Likely to be a
	 * participant's uuid.
	 */
	final protected UUID id;

	/**
	 * Create a NetworkAddress given this UUID.
	 * 
	 * @param id
	 */
	@Inject
	public NetworkAddress(@Assisted UUID id) {
		if (id == null) {
			throw new NullPointerException(
					"Attempting to instantiate a NetworkAddress with null id.");
		}
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NetworkAddress: " + this.id.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NetworkAddress other = (NetworkAddress) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
