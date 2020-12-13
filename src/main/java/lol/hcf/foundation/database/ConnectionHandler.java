package lol.hcf.foundation.database;

import com.mongodb.client.MongoClient;
import redis.clients.jedis.Jedis;

public interface ConnectionHandler {

    /**
     * @return Returns the connection instance to the Mongo Database
     */
    MongoClient getDatabase();

    /**
     * Returns a Jedis instance to the Redis server
     *
     * <strong>Ensure to call {@link AutoCloseable#close()} after to return the object to the pool.</strong>
     * @return Returns a {@link Jedis} retrieved from a pool.
     */
    Jedis getRedis();
}
