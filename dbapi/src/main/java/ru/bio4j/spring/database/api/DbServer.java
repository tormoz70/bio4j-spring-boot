package ru.bio4j.spring.database.api;

public interface DbServer {

    void startServer();
    void shutdownServer();
    String getActualTcpPort();
}
