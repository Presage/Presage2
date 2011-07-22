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
package uk.ac.imperial.presage2.db.sql;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.ac.imperial.presage2.core.db.Table;
import uk.ac.imperial.presage2.db.sql.SQL.CreateTableQueryBuilder;
import uk.ac.imperial.presage2.db.sql.SQL.InsertQueryBuilder;

/**
 * Implementation of a {@link Table} to create queries with the {@link SQL} API.
 * 
 * @author Sam Macbeth
 * 
 */
public class SQLTable implements Table {

	final SQLStorage storage;

	final String tableName;

	Class<?> owner;

	Map<String, Class<?>> fields = new LinkedHashMap<String, Class<?>>();

	String[] primaryKey;

	Set<String[]> indices = new HashSet<String[]>();

	public SQLTable(String tableName, SQLStorage storage) {
		this.tableName = tableName;
		this.storage = storage;
		fields.put("simID", Long.class);
	}

	static class SQLTableBuilder implements Table.TableBuilder {

		final SQLTable table;

		boolean oneRowPerCycle = false;
		boolean oneRowPerSimulation = false;
		boolean participantField = false;

		String[] fieldNames = new String[0];
		Class<?>[] types = new Class<?>[0];

		SQLTableBuilder(String tableName, SQLStorage storage) {
			super();
			table = new SQLTable(tableName, storage);
		}

		@Override
		public TableBuilder forClass(Class<?> classname) {
			table.owner = classname;
			return this;
		}

		@Override
		public TableBuilder withFields(String... fields) {
			this.fieldNames = fields;
			return this;
		}

		@Override
		public TableBuilder withTypes(Class<?>... types) {
			this.types = types;
			return this;
		}

		@Override
		public TableBuilder withOneRowPerTimeCycle() {
			this.oneRowPerCycle = true;
			return this;
		}

		@Override
		public TableBuilder withOneRowPerSimulation() {
			this.oneRowPerSimulation = true;
			return this;
		}

		@Override
		public TableBuilder withParticipantField() {
			this.participantField = true;
			return this;
		}

		@Override
		public Table create() throws Exception {
			if (oneRowPerCycle && oneRowPerSimulation) {
				// TODO: typed checked exception
				throw new RuntimeException(
						"Cannot have both one row per cycle and one row per simulation");
			}

			// add a time row?
			if (oneRowPerCycle) {
				this.table.fields.put("time", Integer.class);
			}
			// add a participant row?
			if (participantField) {
				this.table.fields.put("participantID", UUID.class);
			}
			// build fields
			if (this.fieldNames.length != this.types.length) {
				throw new RuntimeException("Field names and types mismatch");
			}
			for (int i = 0; i < this.fieldNames.length; i++) {
				this.table.fields.put(this.fieldNames[i], this.types[i]);
			}

			// do indices and keys
			Set<String> pkey = new HashSet<String>();
			if (oneRowPerCycle) {
				pkey.add("simID");
				pkey.add("time");
			}
			if (oneRowPerSimulation) {
				pkey.add("simID");
			}
			if (participantField && pkey.size() > 0) {
				pkey.add("participantID");
			}
			this.table.primaryKey = pkey.toArray(new String[0]);

			String[] simIDIndex = { "simID" };
			this.table.indices.add(simIDIndex);

			if (!table.storage.tableExists(table.getTableName())) {
				CreateTableQueryBuilder c = table.storage.createTable(table
						.getTableName());
				for (Map.Entry<String, Class<?>> field : table.fields
						.entrySet()) {
					c.addColumn(field.getKey(), field.getValue());
				}
				c.addConstraints().addPrimaryKey(pkey.toArray(new String[0]));
				c.commit();
			}
			// TODO foreign key

			return this.table;
		}

	}

	@Override
	public Insertion insert() {
		return new Insertion() {

			Map<String, Object> data = new LinkedHashMap<String, Object>();
			final int time = storage.getTime();

			@Override
			public Insertion set(String column, Object value) {
				if (fields.containsKey(column)) {
					data.put(column, value);
				}
				return this;
			}

			@Override
			public Insertion forParticipant(UUID id) {
				if (fields.containsKey("participantID")) {
					data.put("participantID", id);
				}
				return this;
			}

			@Override
			public Insertion atTimeStep(int time) {
				if (fields.containsKey("time")) {
					data.put("time", time);
				}
				return this;
			}

			@Override
			public Insertion atCurrentTimeStep() {
				if (fields.containsKey("time")) {
					data.put("time", time);
				}
				return this;
			}

			@Override
			public void commit() throws SQLException {
				InsertQueryBuilder insert = storage.insertInto(getTableName());
				insert.addColumn("simID", storage.getSimulationId());
				for (Map.Entry<String, Object> column : data.entrySet()) {
					insert.addColumn(column.getKey(), column.getValue());
				}
				insert.commit();
			}
		};
	}

	@Override
	public String getTableName() {
		String name = "";
		if (this.owner != null) {
			name += this.owner.getSimpleName() + "_";
		}
		name += this.tableName;
		return name;
	}

}
