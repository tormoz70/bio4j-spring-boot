package ru.bio4j.spring.model.transport;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix= "database")
public class DatabaseConnectionsProperties {

    private List<ConnectionProperties> connections;

    public List<ConnectionProperties> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionProperties> connections) {
        this.connections = connections;
    }

    public static class PoolProperties {
        private String connectionTimeout;
        private String minimumPoolSize;
        private String maximumPoolSize;

        public String getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(String connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public String getMinimumPoolSize() {
            return minimumPoolSize;
        }

        public void setMinimumPoolSize(String minimumPoolSize) {
            this.minimumPoolSize = minimumPoolSize;
        }

        public String getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(String maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }
    }

    public static class ConnectionProperties {

        private PoolProperties pool;

        private String dbmsName;
        private String driverClassName;
        private String url;
        private String username;
        private String password;

        private String currentSchema;

        public String getDbmsName() {
            return dbmsName;
        }

        public void setDbmsName(String dbmsName) {
            this.dbmsName = dbmsName;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCurrentSchema() {
            return currentSchema;
        }

        public void setCurrentSchema(String currentSchema) {
            this.currentSchema = currentSchema;
        }

        public PoolProperties getPool() {
            return pool;
        }

        public void setPool(PoolProperties pool) {
            this.pool = pool;
        }
    }

}
