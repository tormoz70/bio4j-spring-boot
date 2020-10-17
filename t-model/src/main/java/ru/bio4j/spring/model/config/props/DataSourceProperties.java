package ru.bio4j.spring.model.config.props;

public class DataSourceProperties {

    private String dbmsName;
    private String dbServerPort;
    private String currentSchema;
    private String connectionTimeout;
    private String minimumPoolSize;
    private String maximumPoolSize;
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public static class Builder {
        private String dbmsName;
        private String dbServerPort;
        private String currentSchema;
        private String connectionTimeout;
        private String minimumPoolSize;
        private String maximumPoolSize;
        private String driverClassName;
        private String url;
        private String username;
        private String password;

        public Builder dbmsName(String value) {
            this.dbmsName = value;
            return this;
        }
        public Builder dbServerPort(String value) {
            this.dbServerPort = value;
            return this;
        }
        public Builder currentSchema(String value) {
            this.currentSchema = value;
            return this;
        }
        public Builder connectionTimeout(String value) {
            this.connectionTimeout = value;
            return this;
        }
        public Builder minimumPoolSize(String value) {
            this.minimumPoolSize = value;
            return this;
        }
        public Builder maximumPoolSize(String value) {
            this.maximumPoolSize = value;
            return this;
        }
        public Builder driverClassName(String value) {
            this.driverClassName = value;
            return this;
        }
        public Builder url(String value) {
            this.url = value;
            return this;
        }
        public Builder username(String value) {
            this.username = value;
            return this;
        }
        public Builder password(String value) {
            this.password = value;
            return this;
        }

        public DataSourceProperties build() {
            DataSourceProperties result = new DataSourceProperties();
            result.setDbmsName(dbmsName);
            result.setDbServerPort(dbServerPort);
            result.setCurrentSchema(currentSchema);
            result.setConnectionTimeout(connectionTimeout);
            result.setMinimumPoolSize(minimumPoolSize);
            result.setMaximumPoolSize(maximumPoolSize);
            result.setDriverClassName(driverClassName);
            result.setUrl(url);
            result.setUsername(username);
            result.setPassword(password);
            return result;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDbmsName() {
        return dbmsName;
    }

    public void setDbmsName(String dbmsName) {
        this.dbmsName = dbmsName;
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

    public String getCurrentSchema() {
        return currentSchema;
    }

    public void setCurrentSchema(String currentSchema) {
        this.currentSchema = currentSchema;
    }
    
    public String getDbServerPort() {
        return dbServerPort;
    }

    public void setDbServerPort(String dbServerPort) {
        this.dbServerPort = dbServerPort;
    }

}
