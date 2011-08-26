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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

class JedisPoolUser {

	protected final JedisPool pool;

	protected JedisPoolUser(JedisPool pool) {
		super();
		this.pool = pool;
	}

	protected final int getInt(String key) {
		final Jedis r = pool.getResource();
		try {
			String value = r.get(key);
			if (value != "nil")
				return Integer.parseInt(value);
		} finally {
			pool.returnResource(r);
		}
		return 0;
	}

	protected final void setInt(String key, int value) {
		final Jedis r = pool.getResource();
		try {
			r.set(key, Integer.valueOf(value).toString());
		} finally {
			pool.returnResource(r);
		}
	}

	protected final long getLong(String key) {
		final Jedis r = pool.getResource();
		try {
			String value = r.get(key);
			if (value != "nil")
				return Long.parseLong(value);
		} finally {
			pool.returnResource(r);
		}
		return 0L;
	}

	protected final void setLong(String key, long value) {
		final Jedis r = pool.getResource();
		try {
			r.set(key, Long.valueOf(value).toString());
		} finally {
			pool.returnResource(r);
		}
	}

	protected final String getString(String key) {
		final Jedis r = pool.getResource();
		try {
			String value = r.get(key);
			if (value != "nil")
				return value;
		} finally {
			pool.returnResource(r);
		}
		return null;
	}

	protected final void setString(String key, String value) {
		final Jedis r = pool.getResource();
		try {
			r.set(key, value);
		} finally {
			pool.returnResource(r);
		}
	}

}
