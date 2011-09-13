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
package uk.ac.imperial.presage2.db.redis;

import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;

import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class RedisModule extends DatabaseModule {

	final String host;
	final JedisPoolConfig config = new JedisPoolConfig();

	public RedisModule() {
		super();
		host = "localhost";
	}

	public RedisModule(Properties props) {
		super();
		host = props.getProperty("redis.host", "localhost");
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("redis.host")).toInstance(host);
		bind(JedisPoolConfig.class).toInstance(config);

		bind(RedisDatabase.class).in(Singleton.class);
		bind(DatabaseService.class).to(RedisDatabase.class);
		bind(StorageService.class).to(RedisDatabase.class);
		bind(JedisPool.class).toProvider(RedisDatabase.class);
	}

}
