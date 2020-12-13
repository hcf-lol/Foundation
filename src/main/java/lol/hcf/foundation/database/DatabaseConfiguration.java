package lol.hcf.foundation.database;

import lol.hcf.foundation.data.impl.yml.ConfigurationFile;

import java.io.File;

public class DatabaseConfiguration extends ConfigurationFile {

    public final MongoConfiguration database = new MongoConfiguration();
    public final RedisConfiguration redis = new RedisConfiguration();

    public DatabaseConfiguration(File configFile) {
        super(configFile);
        super.load();
    }

    private DatabaseConfiguration() {
        super(null);
    }

    public static class MongoConfiguration {
        private final String host = "127.0.0.1";
        private final int port = 27017;
        private final String user = "root";
        private final String password = "password";

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class RedisConfiguration {
        private final String host = "127.0.0.1";
        private final int port = 6379;
        private final String auth = null;

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getAuth() {
            return auth;
        }
    }
}
