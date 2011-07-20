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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.Provider;

import uk.ac.imperial.presage2.db.DatabaseService;

public class SQLService implements DatabaseService, Provider<Connection> {

	protected final Logger logger = Logger.getLogger(SQLService.class);
	protected Connection conn;

	final private String connectionurl;

	final private Properties connectionProps;

	boolean started = false;

	protected SQLService(String driver, String connectionurl,
			Properties connectionProps) throws ClassNotFoundException {
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("Loading JDBC driver " + driver);
		}
		Class.forName(driver);
		this.connectionurl = connectionurl;
		this.connectionProps = connectionProps;
	}

	@Override
	public void start() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Starting JDBC connection to " + this.connectionurl);
		}
		conn = DriverManager.getConnection(this.connectionurl,
				this.connectionProps);
		started = true;
	}

	@Override
	public void stop() {
		try {
			conn.close();
		} catch (SQLException e) {
			logger.warn("Failed to close connection", e);
		}
	}

	@Override
	public Connection get() {
		return this.conn;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

}
