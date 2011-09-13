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
package uk.ac.imperial.presage2.db;

import java.util.UUID;

import uk.ac.imperial.presage2.core.participant.Participant;

/**
 * This is an abstraction of a Table in a database. We provide an API for
 * defining a table schema for an existing or to be created table, and to insert
 * data into it. This API is designed purely for dumping simulation data into a
 * data store and as such provides to select or update functionality. For this
 * you should use a lower level API to the database.
 * 
 * @author Sam Macbeth
 * 
 */
@Deprecated
public interface Table {

	/**
	 * Get the full name of this table.
	 * 
	 * @return {@link String} table name.
	 */
	public String getTableName();

	interface TableBuilder {

		/**
		 * Set the class which 'owns' this table. This class name is prefixed to
		 * the table name.
		 * 
		 * @param classname
		 * @return {@link TableBuilder} this.
		 */
		public TableBuilder forClass(Class<?> classname);

		/**
		 * The field names to add to this table.
		 * 
		 * @param fields
		 * @return {@link TableBuilder} this
		 */
		public TableBuilder withFields(String... fields);

		/**
		 * The corresponding types of the fields as defined by
		 * {@link #withFields(String...)}.
		 * 
		 * @param types
		 * @return {@link TableBuilder} this
		 */
		public TableBuilder withTypes(Class<?>... types);

		/**
		 * Add a {@link Participant} field to the table.
		 * 
		 * @return {@link TableBuilder} this
		 */
		public TableBuilder withParticipantField();

		/**
		 * Enforce that there is one row of data per time cycle. If
		 * {@link #withParticipantField()} is specified this is one row per
		 * agent per time cycle.
		 * 
		 * @return {@link TableBuilder} this
		 */
		public TableBuilder withOneRowPerTimeCycle();

		/**
		 * Enforce that there is one row of data for a whole simulation. If
		 * {@link #withParticipantField()} is specified this is one row per
		 * agent per simulation.
		 * 
		 * @return {@link TableBuilder} this
		 */
		public TableBuilder withOneRowPerSimulation();

		/**
		 * Create this {@link Table} object given the previously specified
		 * fields and settings. If the table doesn't not already exist it should
		 * be submitted to the database.
		 * 
		 * @return {@link Table}
		 * @throws Exception
		 */
		public Table create() throws Exception;

	}

	/**
	 * Start an insertion into this table.
	 * 
	 * @return new {@link Insertion}
	 */
	public Insertion insert();

	interface Insertion {

		/**
		 * Set the value of <code>column</code> to <code>value</code>.
		 * 
		 * @param column
		 * @param value
		 * @return {@link Insertion} this
		 */
		public Insertion set(String column, Object value);

		/**
		 * Set the participant column on this insert to id.
		 * 
		 * @param id
		 * @return {@link Insertion} this
		 */
		public Insertion forParticipant(UUID id);

		/**
		 * Set the time column on this insert to time.
		 * 
		 * @param time
		 * @return {@link Insertion} this
		 */
		public Insertion atTimeStep(int time);

		/**
		 * Set the time column on this insert to the current simulation time.
		 * 
		 * @return
		 */
		public Insertion atCurrentTimeStep();

		/**
		 * Commit this insert to the database.
		 * 
		 * @throws Exception
		 */
		public void commit() throws Exception;

	}

}
