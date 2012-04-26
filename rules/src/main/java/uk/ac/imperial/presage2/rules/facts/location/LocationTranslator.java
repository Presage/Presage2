/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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
package uk.ac.imperial.presage2.rules.facts.location;

import java.io.Serializable;
import java.util.UUID;

import uk.ac.imperial.presage2.rules.facts.AgentStateTranslator;

public class LocationTranslator implements AgentStateTranslator {

	@Override
	public boolean canTranslate(String name) {
		return name.equalsIgnoreCase("util.location");
	}

	@Override
	public Object getFactObject(String name, UUID aid, Serializable value) {
		return new Location(aid,
				(uk.ac.imperial.presage2.util.location.Location) value);
	}

	@Override
	public Serializable getStateFromFact(Object fact) {
		return ((Location) fact).getLocation();
	}

}
