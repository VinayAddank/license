package org.rta.citizen.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserSessionModel extends BaseModel  implements UserDetails{

    private static final long serialVersionUID = -3667917921204723157L;

    private Long sessionId;
    private String aadharNumber;
    private Status completionStatus;
    private KeyType keyType;
    private Long loginTime;
    private String uniqueKey;
    private ServiceType serviceType;
    
    public UserSessionModel(Long id, String aadharNumber, Status completionStatus, String uniqueKey,
            ServiceType serviceType) {
        this.sessionId = id;
        this.aadharNumber = aadharNumber;
        this.completionStatus = completionStatus;
        this.uniqueKey = uniqueKey;
        this.serviceType = serviceType;
    }

    public UserSessionModel() {
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public Status getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(Status completionStatus) {
        this.completionStatus = completionStatus;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> userRoles = new ArrayList<String>();
        userRoles.add(serviceType.getCode());
        String roles = StringUtils.collectionToCommaDelimitedString(userRoles);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return uniqueKey;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return aadharNumber;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        if(2 == completionStatus.getValue()) {
            return true;
        } else {
            return false;
        }
    }
}
