package ru.bio4j.spring.model;


import org.springframework.beans.factory.annotation.Value;

public class Sso2ClientProperties {
    @Value("${ss2client.sso2.service.url}")
    private String sso2ServiceUrl;

    public String getSso2ServiceUrl() {
        return sso2ServiceUrl;
    }

    public void setSso2ServiceUrl(String sso2ServiceUrl) {
        this.sso2ServiceUrl = sso2ServiceUrl;
    }
}
