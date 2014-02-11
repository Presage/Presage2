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
package uk.ac.imperial.presage2.core.cli;

import java.util.Map;

import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class Simulation {

	String name;
	String className;
	int finishTime;
	Map<String, String> parameters;

	public PersistentSimulation insert(StorageService sto) {
		PersistentSimulation sim = sto.createSimulation(name, className,
				"AUTO START", finishTime);
		sim.addParameter("finishTime", Integer.toString(finishTime));
		for (Map.Entry<String, String> e : parameters.entrySet()) {
			sim.addParameter(e.getKey(), e.getValue());
		}
		return sim;
	}

}
