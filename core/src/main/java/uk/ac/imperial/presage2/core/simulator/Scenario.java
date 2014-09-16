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
package uk.ac.imperial.presage2.core.simulator;

import java.util.HashSet;
import java.util.Set;

import uk.ac.imperial.presage2.core.TimeDriven;

public class Scenario {

	Set<Object> agents = new HashSet<Object>();
	Set<Object> objects = new HashSet<Object>(); // incl finish conditions

	Scenario() {
		super();
	}

	public void addAgent(Object o) {
		agents.add(o);
	}

	public void addObject(Object o) {
		objects.add(o);
	}

	@Deprecated
	public void addTimeDriven(TimeDriven object) {
		objects.add(object);
	}

	@Deprecated
	public void addEnvironment(TimeDriven object) {
		// Environment handled in simulator.
	}

}
