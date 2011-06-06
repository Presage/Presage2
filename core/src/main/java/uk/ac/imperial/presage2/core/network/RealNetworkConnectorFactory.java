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

import java.util.UUID;

/**
 * @author Sam Macbeth
 *
 */
public class RealNetworkConnectorFactory implements NetworkConnectorFactory {
	
	/**
	 * @see uk.ac.imperial.presage2.core.network.NetworkConnectorFactory#create(java.util.UUID)
	 */
	@Override
	public NetworkConnector create(UUID id) {
		/* 
		 * TODO This method should pull the spec for the participant given by
		 * the given UUID from the simulation config and instantiate it.
		 * 
		 * This would also be a good time to register the NetworkConnector with the 
		 * NetworkController.
		 */
		return null;
	}

}
