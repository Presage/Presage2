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
package uk.ac.imperial.presage2.db.sql;

class Sql {

	static Dialect dialect = Dialect.MYSQL;

	static String formatQuery(String query) {
		switch (dialect) {
		case POSTGRESQL:
		case POSTGRESQL_HSTORE:
			String q = query.replace("`", "\"");
			return q;
		case MYSQL:
		default:
			return query;
		}
	}

	static String createSimulationTable() {
		switch (dialect) {
		case POSTGRESQL:
			return "CREATE TABLE IF NOT EXISTS simulations"
					+ "(id bigserial NOT NULL," + "name varchar(255) NOT NULL,"
					+ "state varchar(80) NOT NULL,"
					+ "classname varchar(255) NOT NULL,"
					+ "\"t\" int NOT NULL DEFAULT 0,"
					+ "\"finishTime\" int NOT NULL,"
					+ "\"createdAt\" bigint NOT NULL DEFAULT 0,"
					+ "\"startedAt\" bigint NOT NULL DEFAULT 0,"
					+ "\"finishedAt\" bigint NOT NULL DEFAULT 0,"
					+ "parent bigint NULL," + " PRIMARY KEY (id)" + ")";
		case POSTGRESQL_HSTORE:
			return "CREATE TABLE IF NOT EXISTS simulations"
					+ "(id bigserial NOT NULL," + "name varchar(255) NOT NULL,"
					+ "state varchar(80) NOT NULL,"
					+ "classname varchar(255) NOT NULL,"
					+ "\"t\" int NOT NULL DEFAULT 0,"
					+ "\"finishTime\" int NOT NULL,"
					+ "\"createdAt\" bigint NOT NULL DEFAULT 0,"
					+ "\"startedAt\" bigint NOT NULL DEFAULT 0,"
					+ "\"finishedAt\" bigint NOT NULL DEFAULT 0,"
					+ "parent bigint NULL, "
					+ "parameters hstore default hstore(array[]::varchar[]),"
					+ " PRIMARY KEY (id)" + ")";
		case MYSQL:
		default:
			return "CREATE TABLE IF NOT EXISTS simulations"
					+ "(`id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`state` varchar(80) NOT NULL,"
					+ "`classname` varchar(255) NOT NULL,"
					+ "`t` int(11) NOT NULL DEFAULT 0,"
					+ "`finishTime` int(11) NOT NULL,"
					+ "`createdAt` bigint(20) NOT NULL DEFAULT 0,"
					+ "`startedAt` bigint(20) NOT NULL DEFAULT 0,"
					+ "`finishedAt` bigint(20) NOT NULL DEFAULT 0,"
					+ "`parent` bigint(20) NULL," + " PRIMARY KEY (`id`)" + ")";
		}
	}

	static String insertIntoSimulations() {
		switch (dialect) {
		case POSTGRESQL:
		case POSTGRESQL_HSTORE:
			return "INSERT INTO simulations (\"name\", state, classname, \"finishTime\", \"createdAt\")"
					+ "VALUES (?, ?, ?, ?, ?)";
		case MYSQL:
		default:
			return "INSERT INTO simulations (`name`, `state`, `classname`, `finishTime`, `createdAt`)"
					+ "VALUES (?, ?, ?, ?, ?)";
		}
	}

	static String createParametersTable() {
		switch (dialect) {
		case POSTGRESQL:
			return "CREATE TABLE IF NOT EXISTS parameters"
					+ "(\"simId\" bigint NOT NULL REFERENCES simulations,"
					+ "name varchar(255) NOT NULL,"
					+ "value varchar(255) NOT NULL,"
					+ "PRIMARY KEY (\"simId\", name))";
		case POSTGRESQL_HSTORE:
			return "";
		case MYSQL:
		default:
			return "CREATE TABLE IF NOT EXISTS parameters"
					+ "(`simId` bigint(20) NOT NULL,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`value` varchar(255) NOT NULL,"
					+ "PRIMARY KEY (`simId`, `name`), INDEX (`simId`))";
		}
	}

	static String insertIntoParameters() {
		switch (dialect) {
		case POSTGRESQL:
			return "INSERT INTO parameters (\"simId\", \"name\", \"value\")"
					+ "	SELECT ?, ?, ?"
					+ "	WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE \"simId\"=? AND \"name\"=?);";
		case MYSQL:
		default:
			return "INSERT INTO parameters " + "(`simId`, `name`, `value`) "
					+ "VALUES " + "(?, ?, ?) "
					+ "ON DUPLICATE KEY UPDATE `value` = VALUES(`value`);";
		}
	}

	static String updateParameters() {
		switch (dialect) {
		case POSTGRESQL:
			return "UPDATE parameters SET \"value\"=? WHERE \"simId\"=? AND \"name\"=?;";
		case MYSQL:
		default:
			return "";
		}
	}

	static String getSimulations() {
		switch (dialect) {
		case POSTGRESQL:
		case POSTGRESQL_HSTORE:
			return "SELECT id, name, state, classname, \"t\", \"finishTime\", "
					+ "\"createdAt\", \"startedAt\", \"finishedAt\", parent "
					+ "FROM simulations ORDER BY id ASC";
		case MYSQL:
		default:
			return "SELECT `id`, `name`, `state`, `classname`, `t`, `finishTime`, "
					+ "`createdAt`, `startedAt`, `finishedAt`, `parent` "
					+ "FROM simulations ORDER BY `id` ASC";
		}
	}

	static String getParametersById() {
		switch (dialect) {
		case POSTGRESQL:
			return "SELECT name, value FROM parameters WHERE \"simId\" = ?";
		case POSTGRESQL_HSTORE:
			return "SELECT (p.params).key, (p.params).value FROM "
					+ "(SELECT each(parameters) AS params FROM simulations WHERE id = ?) AS p";
		case MYSQL:
		default:
			return "SELECT `name`, `value` FROM parameters WHERE `simId` = ?";
		}
	}

}
