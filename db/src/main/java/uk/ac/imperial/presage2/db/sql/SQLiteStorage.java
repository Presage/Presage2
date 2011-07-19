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

import uk.ac.imperial.presage2.core.simulator.RunnableSimulation;

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

	boolean tableExists(String tableName) throws SQLException {
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

}
