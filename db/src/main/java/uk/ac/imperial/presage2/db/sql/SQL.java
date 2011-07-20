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

public interface SQL {

	CreateTableQueryBuilder createTable(String tableName);

	boolean tableExists(String tableName) throws SQLException;

	interface CanCommit {
		void commit() throws SQLException;
	}

	interface CreateTableQueryBuilder extends CanCommit {

		CreateTableQueryBuilder addColumn(String name, Class<?> type);

		<T> CreateTableQueryBuilder addColumn(String name, Class<T> type,
				T defaultValue);

		CreateTableQueryBuilder addColumn(String name, Class<?> type,
				boolean isNull);

		CreateTableQueryBuilder addAutoIncrementColumn(String name,
				Class<?> type);

		CreateTableConstraintsBuilder addConstraints();

	}

	interface CreateTableConstraintsBuilder extends CanCommit {

		CreateTableConstraintsBuilder addPrimaryKey(String... columns);

		CreateTableConstraintsBuilder addIndex(String... columns);

		ForeignKeyDef addForeignKey(String... columns);

	}

	interface ForeignKeyDef {

		CreateTableConstraintsBuilder references(String tableName,
				String... columns);

	}

	InsertQueryBuilder insertInto(String tableName);

	interface InsertQueryBuilder extends CanCommit {

		<T> InsertQueryBuilder addColumn(String name, T value);

		long getInsertedId() throws SQLException;

	}

}
