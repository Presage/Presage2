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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SQLiteStorage extends SQLStorage {

	PreparedStatement checkTableExistance;
	PreparedStatement getTableColumns;

	protected SQLiteStorage(String connectionurl, Properties connectionProps)
			throws ClassNotFoundException {
		super("org.sqlite.JDBC", connectionurl, connectionProps);
	}

	@Override
	public void start() throws Exception {
		super.start();
		// prepare statements
		checkTableExistance = this.conn
				.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=? ");
		// getTableColumns =
		// this.conn.prepareStatement("PRAGMA table_info('?')");
	}

	@Override
	public long getSimulationId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void createTable(SQLTable t) throws SQLException {
		// check if table exists
		if (logger.isDebugEnabled()) {
			logger.debug("Creating table " + t.getTableName());
		}
		if (!tableExists(t.getTableName())) {
			logger.info("Creating new table for " + t.getTableName());
			Statement s = this.conn.createStatement();
			String query = "CREATE TABLE " + t.getTableName() + "( ";
			// fields
			List<String> fields = new LinkedList<String>();
			for (Map.Entry<String, Class<?>> field : t.fields.entrySet()) {
				fields.add(field.getKey() + " " + getSQLType(field.getValue()));
			}
			for (Iterator<String> iterator = fields.iterator(); iterator
					.hasNext();) {
				String string = (String) iterator.next();
				query += string;
				if (iterator.hasNext())
					query += ", ";
			}
			if (t.primaryKey.length > 0) {
				query += ", PRIMARY KEY (";
				for (int i = 0; i < t.primaryKey.length; i++) {
					query += t.primaryKey[i];
					if (i < t.primaryKey.length - 1)
						query += ", ";
				}
				query += ") ";
			}
			query += ") ";
			s.execute(query);
		}

	}

	private String getSQLType(Class<?> value) {
		if (value == Integer.class || value == Long.class) {
			return "INTEGER";
		} else if (value == Float.class || value == Double.class) {
			return "REAL";
		} else if (value == String.class) {
			return "TEXT";
		}
		return "NULL";
	}

	@Override
	public boolean tableExists(String tableName) throws SQLException {
		try {
			checkTableExistance.setString(1, tableName);
			ResultSet res = checkTableExistance.executeQuery();
			return res.next() != false;
		} catch (SQLException e) {
			logger.warn("Exception thrown when checking table " + tableName
					+ " exists.", e);
			throw e;
		}
	}

	@Override
	public CreateTableQueryBuilder createTable(String tableName) {
		return new SQLiteCreateTableQueryBuilder(tableName);
	}

	protected void executeQuery(String query) throws SQLException {
		if (logger.isDebugEnabled())
			logger.debug("Executing Query: " + query);
		Statement s = this.conn.createStatement();
		s.execute(query);
	}

	class SQLiteCreateTableQueryBuilder implements CreateTableQueryBuilder,
			CreateTableConstraintsBuilder, ForeignKeyDef {

		StringBuilder q;
		int columnCount = 0;

		SQLiteCreateTableQueryBuilder(String tableName) {
			q = new StringBuilder();
			q.append("CREATE TABLE ");
			q.append(tableName);
			q.append(" ( ");
		}

		@Override
		public void commit() throws SQLException {
			q.append(" ) ");
			executeQuery(q.toString());
		}

		@Override
		public CreateTableQueryBuilder addColumn(String name, Class<?> type) {
			if (columnCount > 0) {
				q.append(" , ");
			}
			q.append(name);
			q.append(" ");
			q.append(getSQLType(type));
			columnCount++;
			return this;
		}

		@Override
		public <T> CreateTableQueryBuilder addColumn(String name,
				Class<T> type, T defaultValue) {
			addColumn(name, type);
			q.append(" DEFAULT ");
			q.append(defaultValue);
			return this;
		}

		@Override
		public CreateTableQueryBuilder addColumn(String name, Class<?> type,
				boolean isNull) {
			return addColumn(name, type);
		}

		@Override
		public CreateTableConstraintsBuilder addConstraints() {
			return this;
		}

		@Override
		public CreateTableConstraintsBuilder addPrimaryKey(String... columns) {
			q.append(" , ");
			q.append("PRIMARY KEY ( ");
			q.append(commaSeparatedArray(columns));
			q.append(" ) ");
			return this;
		}

		@Override
		public CreateTableConstraintsBuilder addIndex(String... columns) {
			// SQLite does not have an INDEX declaration in table-constraints
			return this;
		}

		@Override
		public ForeignKeyDef addForeignKey(String... columns) {
			q.append(" , ");
			q.append("FOREIGN KEY ( ");
			q.append(commaSeparatedArray(columns));
			q.append(" ) ");
			return this;
		}

		@Override
		public CreateTableConstraintsBuilder references(String tableName,
				String... columns) {
			q.append("REFERENCES ");
			q.append(tableName);
			if (columns.length > 0) {
				q.append("( ");
				q.append(commaSeparatedArray(columns));
				q.append(" ) ");
			}
			return this;
		}

		private String commaSeparatedArray(String... array) {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				s.append(array[i]);
				if (i + 1 < array.length)
					s.append(" , ");
			}
			return s.toString();
		}

	}

}
