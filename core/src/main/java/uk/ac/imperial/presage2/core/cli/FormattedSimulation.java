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
package uk.ac.imperial.presage2.core.cli;

import java.util.HashMap;
import java.util.Map;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

class FormattedSimulation {

	enum Column {
		ID, Name, ClassName, State, SimCycle
	};

	Map<Column, String> fields = new HashMap<Column, String>();

	FormattedSimulation(PersistentSimulation sim) {
		fields.put(Column.ID, String.valueOf(sim.getID()));
		fields.put(Column.Name, sim.getName());
		fields.put(Column.ClassName, sim.getClassName());
		fields.put(Column.State, sim.getState());
		fields.put(Column.SimCycle, sim.getCurrentTime() + "/" + sim.getFinishTime());
	}

	String getField(Column name) {
		return fields.get(name);
	}

}
