package ru.bio4j.spring.database.api;

public interface DBField {

	String getName();

	int getSqlType();

	Class<?> getType();

	Integer getId();
}
