package ru.bio4j.spring.model.transport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.security.Principal;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class User extends SsoUser {

    @JsonIgnore
    @Override
    public String getInnerUid() {
        return getInnerUid();
    }
}
