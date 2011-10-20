package uk.ac.imperial.presage2.db.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import uk.ac.imperial.presage2.db.GenericStorageServiceTest;

public class RedisStorageTest extends GenericStorageServiceTest {

	@Override
	public void getDatabase() {
		// assume there's a redis instance available at localhost or all the
		// tests will fail.
		JedisPoolConfig config = new JedisPoolConfig();
		RedisDatabase redis = new RedisDatabase("localhost", config);

		final Jedis jedis = redis.get().getResource();
		try {
			// clear db 2 for testing
			jedis.select(2);
			jedis.flushDB();
		} finally {
			redis.get().returnResource(jedis);
		}
		this.db = redis;
		this.sto = redis;
	}

}
