package ru.bio4j.spring.model.transport;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public class SsoUser implements Principal, Serializable {

    @ApiModelProperty("Внутренний идентификатор пользователя")
    private String innerUid;
    @ApiModelProperty("Токен безопасности. Передавать в заголовок \"X-SToken\" для всех запросов")
    private String stoken;
    @ApiModelProperty("Дата/время когда истекает срокдействия токена безопасности")
    private LocalDateTime stokenExpire;
    @ApiModelProperty("Токен обновления. Используется для обновления токена безопасности")
    private String refreshToken;
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
    @ApiModelProperty("ID вышестоящей организации по отношению к организации, к которой относится пользователь")
    private String parentOrgId;
    @ApiModelProperty("ID дочерних организаций по отношению к организации, к которой относится пользователь (разделитель \",\")")
    private String childOrgIds;

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

    public LocalDateTime getStokenExpire() {
        return stokenExpire;
    }

    public void setStokenExpire(LocalDateTime stokenExpire) {
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

    public String getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(String parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getChildOrgIds() {
        return childOrgIds;
    }

    public void setChildOrgIds(String childOrgIds) {
        this.childOrgIds = childOrgIds;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SsoUser)) return false;
        SsoUser ssoUser = (SsoUser) o;
        return Objects.equals(innerUid, ssoUser.innerUid) &&
                Objects.equals(stoken, ssoUser.stoken) &&
                Objects.equals(stokenExpire, ssoUser.stokenExpire) &&
                Objects.equals(refreshToken, ssoUser.refreshToken) &&
                Objects.equals(login, ssoUser.login) &&
                Objects.equals(fio, ssoUser.fio) &&
                Objects.equals(email, ssoUser.email) &&
                Objects.equals(phone, ssoUser.phone) &&
                Objects.equals(orgId, ssoUser.orgId) &&
                Objects.equals(orgName, ssoUser.orgName) &&
                Objects.equals(orgDesc, ssoUser.orgDesc) &&
                Objects.equals(parentOrgId, ssoUser.parentOrgId) &&
                Objects.equals(childOrgIds, ssoUser.childOrgIds) &&
                Objects.equals(roles, ssoUser.roles) &&
                Objects.equals(grants, ssoUser.grants) &&
                Objects.equals(remoteIP, ssoUser.remoteIP) &&
                Objects.equals(remoteClient, ssoUser.remoteClient) &&
                Objects.equals(anonymouse, ssoUser.anonymouse) &&
                Objects.equals(deviceuuid, ssoUser.deviceuuid) &&
                Objects.equals(pushenabled, ssoUser.pushenabled) &&
                Objects.equals(pushtoken, ssoUser.pushtoken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innerUid, stoken, stokenExpire, refreshToken, login, fio, email, phone, orgId, orgName, orgDesc, parentOrgId, childOrgIds, roles, grants, remoteIP, remoteClient, anonymouse, deviceuuid, pushenabled, pushtoken);
    }
}
