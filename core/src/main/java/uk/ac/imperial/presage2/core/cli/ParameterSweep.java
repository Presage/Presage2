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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Describes a set of simulation parameters permutations.
 * 
 * @author Sam Macbeth
 * 
 */
public class ParameterSweep extends Experiment {

	String simNamePattern;
	String className;
	int simSteps;
	Map<String, Iterable<String>> parameters = new HashMap<String, Iterable<String>>();

	Iterator<String> parameterIt = null;
	String current = null;
	Map<String, Iterator<String>> currentIterators = null;
	Map<String, String> currentParams;

	boolean hasNext = true;

	public ParameterSweep(String name, String description,
			String simNamePattern, String className, int simSteps) {
		super(name, description);
		this.simNamePattern = simNamePattern;
		this.className = className;
		this.simSteps = simSteps;
	}

	public ParameterSweep(String name, String simNamePattern, String className,
			int simSteps) {
		this(name, "", simNamePattern, className, simSteps);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Experiment addParameter(String name, Iterable<String> values) {
		parameters.put(name, values);
		return this;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Simulation next() {
		if (parameterIt == null)
			throw new RuntimeException(
					"Iterator not initialised, please call build()");
		if (!hasNext)
			throw new NoSuchElementException();

		Simulation s = new Simulation();
		s.className = className;
		s.finishTime = simSteps;
		s.parameters = new HashMap<String, String>(currentParams);
		s.name = formatName(simNamePattern, s.parameters);

		nextState();
		return s;
	}

	void nextState() {
		// increment to next state
		if (currentIterators.get(current).hasNext()) {
			// increment current parameter
			currentParams.put(current, currentIterators.get(current).next());
			return;
		}
		do {
			if (parameterIt.hasNext()) {
				current = parameterIt.next();
			} else {
				hasNext = false;
				return; // no more states
			}
		} while (!currentIterators.get(current).hasNext());

		currentParams.put(current, currentIterators.get(current).next());
		// reset previous params
		for (String param : parameters.keySet()) {
			if (param.equals(current))
				break;
			Iterator<String> it = parameters.get(param).iterator();
			currentIterators.put(param, it);
			currentParams.put(param, it.next());
		}
		parameterIt = parameters.keySet().iterator();
		current = parameterIt.next();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.imperial.presage2.core.cli.Experiment#build()
	 */
	@Override
	public Experiment build() throws InvalidParametersException {
		currentIterators = new HashMap<String, Iterator<String>>();
		currentParams = new HashMap<String, String>();

		// special case: no params. Refuse to build.
		if (parameters.size() == 0) {
			throw new InvalidParametersException(
					"No parameters specified. Cannot build.");
		}

		// build iterator set and initial parameters.
		parameterIt = parameters.keySet().iterator();
		while (parameterIt.hasNext()) {
			// TODO: Error handling when keys don't match.
			final String param = parameterIt.next();
			Iterator<String> it = parameters.get(param).iterator();
			currentIterators.put(param, it);
			currentParams.put(param, it.next());
		}

		// reset parameter iterator and start at first parameter
		parameterIt = parameters.keySet().iterator();
		current = parameterIt.next();

		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"This iterator does not support removal.");
	}

	private static String formatName(String name, Map<String, String> parameters) {
		String formatted = new String(name);
		for (Map.Entry<String, String> e : parameters.entrySet()) {
			formatted = formatted.replace("%{p." + e.getKey() + "}",
					e.getValue());
		}
		return formatted;
	}

}
