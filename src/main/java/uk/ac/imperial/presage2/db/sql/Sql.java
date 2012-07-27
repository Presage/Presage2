package uk.ac.imperial.presage2.db.sql;

class Sql {

	static Dialect dialect = Dialect.MYSQL;

	static String formatQuery(String query) {
		switch (dialect) {
		case POSTGRESQL:
			String q = query.replace("`", "\"");
			System.out.println(q);
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
					+ "\"currentTime\" int NOT NULL DEFAULT 0,"
					+ "\"finishTime\" int NOT NULL,"
					+ "\"createdAt\" bigint NOT NULL DEFAULT 0,"
					+ "\"startedAt\" bigint NOT NULL DEFAULT 0,"
					+ "\"finishedAt\" bigint NOT NULL DEFAULT 0,"
					+ "parent bigint NULL," + " PRIMARY KEY (id)" + ")";
		case MYSQL:
		default:
			return "CREATE TABLE IF NOT EXISTS simulations"
					+ "(`id` bigint(20) NOT NULL AUTO_INCREMENT,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`state` varchar(80) NOT NULL,"
					+ "`classname` varchar(255) NOT NULL,"
					+ "`currentTime` int(11) NOT NULL DEFAULT 0,"
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
		case MYSQL:
		default:
			return "CREATE TABLE IF NOT EXISTS parameters"
					+ "(`simId` bigint(20) NOT NULL,"
					+ "`name` varchar(255) NOT NULL,"
					+ "`value` varchar(255) NOT NULL,"
					+ "PRIMARY KEY (`simId`, `name`), INDEX (`simId`),"
					+ "FOREIGN KEY (`simID`) REFERENCES `simulations` (`ID`) ON DELETE CASCADE)";
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

	public static String updateParameters() {
		switch (dialect) {
		case POSTGRESQL:
			return "UPDATE parameters SET \"value\"=? WHERE \"simId\"=? AND \"name\"=?;";
		case MYSQL:
		default:
			return "";
		}
	}

}
