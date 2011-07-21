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

/**
 * Fluent interface for creating and executing SQL queries.
 * 
 * @author Sam Macbeth
 * 
 */
public interface SQL {

	/**
	 * <p>
	 * Initiates a <code>CREATE TABLE</code> query for a table with the name
	 * <code>tableName</code>.
	 * </p>
	 * 
	 * @param tableName
	 * @return {@link CreateTableQueryBuilder} in order to build a query.
	 */
	CreateTableQueryBuilder createTable(String tableName);

	/**
	 * Check if the table <code>tableName</code> exists in the database.
	 * 
	 * @param tableName
	 * @return true if it does, false otherwise.
	 * @throws SQLException
	 */
	boolean tableExists(String tableName) throws SQLException;

	interface CanCommit {
		/**
		 * <p>
		 * Commit this query to the database.
		 * </p>
		 * <p>
		 * This method doesn't guarantee immediate execution of the query as no
		 * result is required.
		 * </p>
		 * 
		 * @throws SQLException
		 */
		void commit() throws SQLException;
	}

	/**
	 * Intermediate stage of creating a table.
	 * 
	 * @author Sam Macbeth
	 */
	interface CreateTableQueryBuilder extends CanCommit {

		/**
		 * Add a column <code>name</code> to this table which has a type
		 * <code>type</code>. The type will be mapped to a suitable SQL type for
		 * the underlying database.
		 * 
		 * @param name
		 * @param type
		 * @return this
		 */
		CreateTableQueryBuilder addColumn(String name, Class<?> type);

		/**
		 * Add a column <code>name</code> to this table which has a type
		 * <code>type</code> and a default value <code>defaultValue</code>.
		 * 
		 * @param name
		 * @param type
		 * @param defaultValue
		 * @return this
		 */
		<T> CreateTableQueryBuilder addColumn(String name, Class<T> type,
				T defaultValue);

		/**
		 * Add a column <code>name</code> to this table which has a type
		 * <code>type</code> and is nullable.
		 * 
		 * @param name
		 * @param type
		 * @param isNull
		 * @return this
		 */
		CreateTableQueryBuilder addColumn(String name, Class<?> type,
				boolean isNull);

		/**
		 * Add a auto incrementing primary key column <code>name</code> to this
		 * table which has a type <code>type</code>.
		 * 
		 * @param name
		 * @param type
		 * @return this
		 */
		CreateTableQueryBuilder addAutoIncrementColumn(String name,
				Class<?> type);

		/**
		 * Add constraints to this table (primary key, indices, foreign keys).
		 * 
		 * @return {@link CreateTableConstraintsBuilder}
		 */
		CreateTableConstraintsBuilder addConstraints();

	}

	interface CreateTableConstraintsBuilder extends CanCommit {

		/**
		 * Add a primary key to the table on <code>columns</code>
		 * 
		 * @param columns
		 * @return this
		 */
		CreateTableConstraintsBuilder addPrimaryKey(String... columns);

		/**
		 * Add an index on the table across <code>columns</code>.
		 * 
		 * @param columns
		 * @return this
		 */
		CreateTableConstraintsBuilder addIndex(String... columns);

		/**
		 * Add a foreign key to the table from the given columns.
		 * 
		 * @param columns
		 * @return {@link ForeignKeyDef}
		 */
		ForeignKeyDef addForeignKey(String... columns);

	}

	interface ForeignKeyDef {

		/**
		 * Specify which table and columns this foreign key references.
		 * 
		 * @param tableName
		 * @param columns
		 * @return {@link CreateTableConstraintsBuilder}
		 */
		CreateTableConstraintsBuilder references(String tableName,
				String... columns);

	}

	/**
	 * Initiates a <code>INSERT INTO</code> query for the given table.
	 * 
	 * @param tableName
	 * @return {@link InsertQueryBuilder}
	 */
	InsertQueryBuilder insertInto(String tableName);

	interface InsertQueryBuilder extends CanCommit {

		/**
		 * Add this key-value pair to the insert query.
		 * 
		 * @param name
		 * @param value
		 * @return {@link InsertQueryBuilder}
		 */
		<T> InsertQueryBuilder addColumn(String name, T value);

		/**
		 * Commit this insert and return the ID at which it was inserted.
		 * 
		 * @return ID that this row was inserted.
		 * @throws SQLException
		 */
		long getInsertedId() throws SQLException;

	}

	/**
	 * Initiate an <code>UPDATE</code> query on <code>tableName</code>.
	 * 
	 * @param tableName
	 * @return {@link UpdateQueryBuilder}
	 */
	UpdateQueryBuilder update(String tableName);

	interface UpdateQueryBuilder extends CanCommit {

		/**
		 * Set the value of <code>field</code> to <code>value</code>
		 * 
		 * @param field
		 * @param value
		 * @return {@link UpdateQueryBuilder}
		 */
		<T> UpdateQueryBuilder set(String field, T value);

		/**
		 * Add a <code>WHERE</code> clause to the UPDATE to specify that
		 * <code>field</code> equals <code>equals</code>.
		 * 
		 * @param field
		 * @param equals
		 * @return {@link UpdateQueryBuilder}
		 */
		<T> UpdateQueryBuilder whereEquals(String field, T equals);

	}

}
