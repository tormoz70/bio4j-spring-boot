package ru.bio4j.spring.model.transport.jstore;

public enum Fixed {
    LEFT, RIGHT, NONE;

    public int getCode() {
        return this.ordinal();
    }
}
