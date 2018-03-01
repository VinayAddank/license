package org.rta.citizen.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class TokenModel extends BaseModel {

    private static final long serialVersionUID = 3665400640422269434L;

    private String id;
    private String token;
    private List<String> roles;
    private String appNumber;

	private String uid_num;
    private String llrNumber;
    
    public String getUid_num() {
		return uid_num;
	}

	public void setUid_num(String uid_num) {
		this.uid_num = uid_num;
	}

	public String getLlrNumber() {
		return llrNumber;
	}

	public void setLlrNumber(String llrNumber) {
		this.llrNumber = llrNumber;
	}


    public TokenModel() {}

    public TokenModel(String token) {
        this.setToken(token);
    }

    public TokenModel(String token, String id) {
        this.setToken(token);
        this.setId(id);
    }

    public TokenModel(String token, String id, List<String> roles) {
        this.setToken(token);
        this.setId(id);
        this.setRoles(roles);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getAppNumber() {
        return appNumber;
    }

    public void setAppNumber(String appNumber) {
        this.appNumber = appNumber;
    }
}
