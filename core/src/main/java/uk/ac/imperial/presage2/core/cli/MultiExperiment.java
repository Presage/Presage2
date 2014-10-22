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
package uk.ac.imperial.presage2.core.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class MultiExperiment extends Experiment {

	final List<Experiment> experiments = new ArrayList<Experiment>();

	Iterator<Experiment> exprIt = null;
	Experiment current = null;

	public MultiExperiment(String name, String description, Experiment... exprs) {
		super(name, description);
		experiments.addAll(Arrays.asList(exprs));
	}

	public void add(Experiment e) {
		experiments.add(e);
	}

	@Override
	public boolean hasNext() {
		return exprIt.hasNext() || (current != null && current.hasNext());
	}

	@Override
	public Simulation next() {
		if (exprIt == null) {
			throw new RuntimeException(
					"Iterator not initialised, please call build()");
		}
		if (current == null || !current.hasNext()) {
			if (exprIt.hasNext())
				current = exprIt.next();
			else
				throw new NoSuchElementException();
		}
		return current.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"This iterator does not support removal.");
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public Experiment build() throws InvalidParametersException {
		for (Experiment e : experiments) {
			e.build();
		}
		this.exprIt = experiments.iterator();
		return this;
	}

	public Experiment addParameter(String name, Iterable<String> values) {
		for (Experiment e : experiments) {
			e.addParameter(name, values);
		}
		return this;
	}

}
