package ru.bio4j.spring.model.transport;

public enum SecurityRequestType {
    status("status"), login("login"), curuser("curuser"), loggedin("loggedin"), logout("logout"), restoreUser("restoreUser");
    private String path;

    SecurityRequestType(String path) {
        this.path = path;
    }
    public String path() {
        return path;
    }
}
