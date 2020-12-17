package lol.hcf.foundation.database;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ConnectionManager implements ConnectionHandler, AutoCloseable {

    private final MongoClient databaseClient;

    private final JedisPool pool;
    private final String auth;

    public ConnectionManager(DatabaseConfiguration config) {
        StringBuilder connectionString = new StringBuilder();
        connectionString.append("mongodb://");

        if (config.database.getUser() != null) {
            connectionString.append(config.database.getUser());
            if (config.database.getPassword() != null) connectionString.append(':').append(config.database.getPassword());
            connectionString.append('@');
        }

        connectionString.append(config.database.getHost()).append(':').append(config.database.getPort()).append("/?authSource=admin");
        this.databaseClient = MongoClients.create(new ConnectionString(connectionString.toString()));
        this.pool = new JedisPool(config.redis.getHost(), config.redis.getPort());
        this.auth = config.redis.getAuth();
    }

    @Override
    public MongoClient getDatabase() {
        return this.databaseClient;
    }

    @Override
    public Jedis getRedis() {
        Jedis jedis = this.pool.getResource();
        if (this.auth != null) jedis.auth(this.auth);
        return jedis;
    }

    @Override
    public void close() {
        this.databaseClient.close();
        this.pool.close();
    }
}
