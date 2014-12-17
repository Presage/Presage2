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

import java.sql.Connection;
import java.util.Properties;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

public class SqlModule extends DatabaseModule {

	Properties properties;
	Class<? extends SqlStorage> impl;

	final String defaultDriver = "org.postgresql.Driver";
	final String defaultPostgresqlImpl = "uk.ac.imperial.presage2.db.sql.PostgreSQLStorage";
	final String defaultMysqlImpl = "uk.ac.imperial.presage2.db.sql.SqlStorage";
	String implClass = defaultPostgresqlImpl;

	@SuppressWarnings("unchecked")
	public SqlModule(Properties properties) {
		super();
		this.properties = properties;

		// set driver to postgresql if not defined; use SqlStorage
		// implementation if driver is not postgres
		if (!this.properties.containsKey("driver")) {
			this.properties.put("driver", defaultDriver);
		}
		implClass = this.properties.getProperty("implementation");
		if (implClass == null) {
			if (this.properties.get("driver").equals(defaultDriver))
				implClass = defaultPostgresqlImpl;
			else
				implClass = defaultMysqlImpl;
		}
		try {
			impl = (Class<? extends SqlStorage>) Class.forName(properties
					.getProperty("implementation", implClass));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		if (impl == null)
			throw new RuntimeException("Could not find storage class: "
					+ implClass);
	}

	@Override
	protected void configure() {
		bind(Properties.class).annotatedWith(Names.named("sql.info"))
				.toInstance(properties);

		bind(impl).in(Singleton.class);
		bind(DatabaseService.class).to(impl);
		bind(StorageService.class).to(impl);
	}

}
