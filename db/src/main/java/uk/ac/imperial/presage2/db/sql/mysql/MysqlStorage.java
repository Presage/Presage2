package uk.ac.imperial.presage2.db.sql.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import uk.ac.imperial.presage2.db.sql.JDBCProperties;
import uk.ac.imperial.presage2.db.sql.JDBCUrl;
import uk.ac.imperial.presage2.db.sql.SQLStorage;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MysqlStorage extends SQLStorage {

	PreparedStatement checkTableExistance = null;

	@Inject
	protected MysqlStorage(@JDBCUrl String connectionurl,
			@JDBCProperties Properties connectionProps)
			throws ClassNotFoundException {
		super("com.mysql.jdbc.Driver", connectionurl, connectionProps);
	}

	private String getSQLType(Class<?> value) {
		if (value == Integer.class || value == int.class) {
			return "INT";
		} else if (value == Long.class || value == long.class) {
			return "BIGINT";
		} else if (value == Float.class || value == float.class) {
			return "FLOAT";
		} else if (value == Double.class || value == double.class) {
			return "REAL";
		} else if (value == String.class) {
			return "VARCHAR(1024)";
		} else if (value == UUID.class) {
			return "VARCHAR(37)";
		} else if (value == Time.class) {
			return "TIMESTAMP";
		}
		return "NULL";
	}

	@Override
	public boolean tableExists(String tableName) throws SQLException {
		if (checkTableExistance == null)
			checkTableExistance = this.conn
					.prepareStatement("SHOW TABLES LIKE ? ");

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
		return new MysqlCreateTableQueryBuilder(tableName);
	}

	@Override
	public InsertQueryBuilder insertInto(String tableName) {
		return new MysqlInsertQueryBuilder(tableName);
	}

	@Override
	public UpdateQueryBuilder update(String tableName) {
		return new MysqlUpdateQueryBuilder(tableName);
	}

	class MysqlCreateTableQueryBuilder implements CreateTableQueryBuilder,
			CreateTableConstraintsBuilder, ForeignKeyDef {

		StringBuilder q;
		int columnCount = 0;

		MysqlCreateTableQueryBuilder(String tableName) {
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
			q.append("'" + defaultValue + "'");
			return this;
		}

		@Override
		public CreateTableQueryBuilder addColumn(String name, Class<?> type,
				boolean isNull) {
			return addColumn(name, type);
		}

		@Override
		public CreateTableQueryBuilder addAutoIncrementColumn(String name,
				Class<?> type) {
			addColumn(name, type);
			q.append(" PRIMARY KEY AUTO_INCREMENT ");
			return this;
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

	class MysqlInsertQueryBuilder implements InsertQueryBuilder {

		StringBuilder q = new StringBuilder();

		Map<String, Object> columns = new LinkedHashMap<String, Object>();

		MysqlInsertQueryBuilder(String tableName) {
			q.append("INSERT INTO ");
			q.append(tableName);
		}

		@Override
		public <T> InsertQueryBuilder addColumn(String name, T value) {
			columns.put(name, value);
			return this;
		}

		private void finishQuery() {
			q.append(" ( ");
			q.append(commaSeparatedObjectArray(columns.keySet().toArray()));
			q.append(" ) VALUES ( ");
			Object[] data = new String[columns.size()];
			Arrays.fill(data, "?");
			q.append(commaSeparatedObjectArray(data));
			q.append(" ) ");
		}

		@Override
		public long getInsertedId() throws SQLException {
			finishQuery();
			return insert(q.toString(), columns.values().toArray());
		}

		@Override
		public void commit() throws SQLException {
			finishQuery();
			deferredQuery(q.toString(), columns.values().toArray());
		}

	}

	class MysqlUpdateQueryBuilder implements UpdateQueryBuilder {

		StringBuilder q = new StringBuilder();
		Map<String, Object> columns = new LinkedHashMap<String, Object>();
		Map<String, Object> wheres = new LinkedHashMap<String, Object>();
		int columnCount = 0;
		int whereCount = 0;

		MysqlUpdateQueryBuilder(String tableName) {
			q.append("UPDATE ");
			q.append(tableName);
			q.append(" SET ");
		}

		@Override
		public void commit() throws SQLException {
			if (columnCount == 0 || whereCount == 0) {
				return; // avoid updating nothing or updating everything
			}
			Object[] setClause = new String[columns.size()];
			int i = 0;
			for (String col : columns.keySet()) {
				setClause[i] = col + " = ? ";
				i++;
			}
			q.append(commaSeparatedObjectArray(setClause));
			q.append(" WHERE ");
			Object[] whereClause = new String[wheres.size()];
			i = 0;
			for (String wh : wheres.keySet()) {
				whereClause[i] = wh + " = ? ";
				i++;
			}
			q.append(andSeparatedObjectArray(whereClause));

			Object[] data = new Object[setClause.length + whereClause.length];
			System.arraycopy(columns.values().toArray(), 0, data, 0,
					setClause.length);
			System.arraycopy(wheres.values().toArray(), 0, data,
					setClause.length, whereClause.length);

			deferredQuery(q.toString(), data);
		}

		@Override
		public <T> UpdateQueryBuilder set(String field, T value) {
			columnCount++;
			columns.put(field, value);
			return this;
		}

		@Override
		public <T> UpdateQueryBuilder whereEquals(String field, T equals) {
			whereCount++;
			wheres.put(field, equals);
			return this;
		}

		protected String andSeparatedObjectArray(Object... array) {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				s.append(array[i]);
				if (i + 1 < array.length)
					s.append(" AND ");
			}
			return s.toString();
		}

	}

}
