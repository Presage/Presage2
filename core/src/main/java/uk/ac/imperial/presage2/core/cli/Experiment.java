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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class Experiment implements Iterator<Simulation> {

	String name;
	String description;

	protected Experiment(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public abstract Experiment build() throws InvalidParametersException;

	public abstract Experiment addParameter(String name, Iterable<String> values);

	public Experiment addArrayParameter(String name, Object... values) {
		List<String> strVals = new ArrayList<String>();
		for (Object o : values) {
			strVals.add(o.toString());
		}
		return addParameter(name, strVals);
	}

	public Experiment addArrayParameter(String name, String... values) {
		return addParameter(name, Arrays.asList(values));
	}

	public Experiment addFixedParameter(String name, String value) {
		return addArrayParameter(name, new String[] { value });
	}

	public Experiment addFixedParameter(String name, Object value) {
		return addFixedParameter(name, value.toString());
	}

	public Experiment addRangeParameter(String name, int start, int count,
			int interval) {
		String[] range = new String[count];
		for (int i = 0; i < count; i++) {
			range[i] = Integer.toString(start + i * interval);
		}
		return addParameter(name, Arrays.asList(range));
	}

}