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
import java.util.Properties;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

import com.google.inject.Singleton;

public class SQLModule extends DatabaseModule {

	final Properties jdbcProps;

	public SQLModule(Properties props) {
		this.jdbcProps = props;
	}

	@Override
	protected void configure() {
		bind(Connection.class).toProvider(SQLService.class);
		bind(DatabaseService.class).to(SQLService.class).in(Singleton.class);
		bind(StorageService.class).to(SQLStorage.class).in(Singleton.class);

		bind(Properties.class).annotatedWith(JDBCProperties.class).toInstance(
				jdbcProps);
		bind(String.class).annotatedWith(JDBCUrl.class).toInstance(
				jdbcProps.getProperty("url"));
	}

}
