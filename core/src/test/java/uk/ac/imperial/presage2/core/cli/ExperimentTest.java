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

import static org.junit.Assert.*;

import org.junit.Test;

public class ExperimentTest {

	@Test
	public void testSingleParam() throws InvalidParametersException {
		Experiment e = new ParameterSweep("Test", "Test_%{p.test}",
				"uk.ac.imperial.TestClass", 100);
		final String value = "5";
		e.addArrayParameter("test", new String[] { value });
		e.build();
		assertTrue(e.hasNext());
		Simulation s = e.next();
		assertTrue(s.parameters.get("test").equals(value));
		assertFalse(e.hasNext());
	}

	@Test
	public void testNoParams() {
		try {
			new ParameterSweep("Test", "Test_%{p.test}",
					"uk.ac.imperial.TestClass", 100).build();
			fail();
		} catch (InvalidParametersException e) {
		}
	}

	@Test
	public void testMultipleParam() throws InvalidParametersException {
		Experiment e = new ParameterSweep("Test", "Test_%{p.test}",
				"uk.ac.imperial.TestClass", 100);
		final String value = "5";
		e.addArrayParameter("test", new String[] { value });
		e.build();
		assertTrue(e.hasNext());
		Simulation s = e.next();
		assertTrue(s.parameters.get("test").equals(value));
		assertFalse(e.hasNext());
	}

}
