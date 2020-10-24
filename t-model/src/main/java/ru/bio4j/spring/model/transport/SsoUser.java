package ru.bio4j.spring.model.transport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import ru.bio4j.spring.model.serializers.DateSerializerOpt;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class SsoUser implements Principal, Serializable {

    @ApiModelProperty("Внутренний идентификатор пользователя")
    private String innerUid;
    @ApiModelProperty("Токен безопасности. Передавать в заголовок \"X-SToken\" для всех запросов")
    private String stoken;
    @ApiModelProperty("Дата/время когда истекает срокдействия токена безопасности")
    @JsonSerialize(using = DateSerializerOpt.class)
    @JsonDeserialize
    private Date stokenExpire;
    @ApiModelProperty("Логин пользователя (уникальное имя пользователя, которое используется при входе в систему)")
    private String login;
    @ApiModelProperty("ФИО пользователя")
    private String fio;
    @ApiModelProperty("Email пользователя")
    private String email;
    @ApiModelProperty("Контактная информация пользователя")
    private String phone;
    @ApiModelProperty("ID организации к которой относится пользователь")
    private String orgId;
    @ApiModelProperty("Название организации к которой относится пользователь")
    private String orgName;
    @ApiModelProperty("Описание организации к которой относится пользователь")
    private String orgDesc;

    @ApiModelProperty("Список ролей пользователя (разделитель \",\")")
    private String roles;
    @ApiModelProperty("Список разрешений пользователя (разделитель \",\")")
    private String grants;

    @ApiModelProperty("IP адрес пользователя")
    private String remoteIP;
    @ApiModelProperty("Описание приложения пользователя")
    private String remoteClient;

    @ApiModelProperty("true - анонимный пользователь")
    private Boolean anonymouse;

    @ApiModelProperty("Уникальный идентификатор устройства пользователя")
    private String deviceuuid;

    @ApiModelProperty("true - пуш-уведомления включены")
    private Boolean pushenabled;

    @ApiModelProperty("пуш-токен")
    private String pushtoken;

    public String getStoken() {
        return stoken;
    }

    public String getLogin() {
        return login;
    }

    public String getFio() {
        return fio;
    }

    public String getRoles() {
        return roles;
    }

    public String getGrants() {
        return grants;
    }

    public void setStoken(String stoken) {
        this.stoken = stoken;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setGrants(String grants) {
        this.grants = grants;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getInnerUid() {
        return innerUid;
    }

    public void setInnerUid(String innerUid) {
        this.innerUid = innerUid;
    }

    @Override
    public String getName() {
        return login;
    }

    public String getRemoteClient() {
        return remoteClient;
    }

    public void setRemoteClient(String remoteClient) {
        this.remoteClient = remoteClient;
    }

    public String getDeviceuuid() {
        return deviceuuid;
    }

    public void setDeviceuuid(String deviceuuid) {
        this.deviceuuid = deviceuuid;
    }

    public Boolean getAnonymouse() {
        return anonymouse;
    }

    public void setAnonymouse(Boolean anonymouse) {
        this.anonymouse = anonymouse;
    }

    public Date getStokenExpire() {
        return stokenExpire;
    }

    public void setStokenExpire(Date stokenExpire) {
        this.stokenExpire = stokenExpire;
    }

    public Boolean getPushenabled() {
        return pushenabled;
    }

    public void setPushenabled(Boolean pushenabled) {
        this.pushenabled = pushenabled;
    }

    public String getPushtoken() {
        return pushtoken;
    }

    public void setPushtoken(String pushtoken) {
        this.pushtoken = pushtoken;
    }
}
