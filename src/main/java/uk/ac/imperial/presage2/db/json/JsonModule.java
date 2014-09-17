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
package uk.ac.imperial.presage2.db.json;

import java.util.Properties;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class JsonModule extends DatabaseModule {

	static final String STORAGEPATH_KEY = "json.storagepath";

	String baseStoragePath = "data/";

	public JsonModule(Properties p) {
		super();
		if (p.containsKey(STORAGEPATH_KEY)) {
			baseStoragePath = p.getProperty(STORAGEPATH_KEY);
		}
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named(STORAGEPATH_KEY))
				.toInstance(baseStoragePath);

		bind(JsonStorage.class).in(Singleton.class);
		bind(DatabaseService.class).to(JsonStorage.class);
		bind(StorageService.class).to(JsonStorage.class);
	}

}
