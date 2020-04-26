package ru.bio4j.spring.model.transport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix= "spring.datasource")
public class DataSourceProperties {
    @Value("${ru.bio4j.jdbc-helper.currentSchema}")
    private String currentSchema;

    @Value("${spring.datasource.hikari.connectionTimeout}")
    private String connectionTimeout;
    @Value("${spring.datasource.hikari.minimumPoolSize}")
    private String minimumPoolSize;
    @Value("${spring.datasource.hikari.maximumPoolSize}")
    private String maximumPoolSize;

    private String dbmsName;
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public String getDbmsName() {
        return dbmsName;
    }

    public void setDbmsName(String dbmsName) {
        this.dbmsName = dbmsName;
    }

    public static class Builder {
        private String currentSchema;
        private String connectionTimeout;
        private String minimumPoolSize;
        private String maximumPoolSize;
        private String driverClassName;
        private String url;
        private String username;
        private String password;

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
}
