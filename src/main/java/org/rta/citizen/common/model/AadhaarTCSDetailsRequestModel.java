package org.rta.citizen.common.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class AadhaarTCSDetailsRequestModel implements Serializable {

	private static final long serialVersionUID = -5224260315487785622L;

	@NotNull(message = "uid_num is missing")
	private String uid_num;

	@NotNull(message = "encryptedPid is missing")
	private String encryptedPid;

	@NotNull(message = "encSessionKey is missing")
	private String encSessionKey;

	@NotNull(message = "encSessionKey is missing")
	private String encHmac;

	@NotNull(message = "DpId is missing")
	private String dpId;
	
	@NotNull(message = "Client request time is missing")
	private String crt;
	
	@NotNull(message = "TID is missing")
	private String tid;
	
	@NotNull(message = "UDC(Mac ID) is missing")
    private String udc;
	
	@NotNull(message = "RdsVer is missing")
    private String rdsVer;
	
	@NotNull(message = "Consent ME is missing")
    private String consentme;
    
    @NotNull(message = "RdsId is missing")
    private String rdsId;
	
	private String attemptType;

	private String pincode;
	
	@NotNull(message = "DC is missing")
    private String dc;
    
	@NotNull(message = "MI is missing")
	private String mi;
	
	@NotNull(message = "MC is missing")
    private String mc;

	public String getUid_num() {
		return uid_num;
	}

	public void setUid_num(String uid_num) {
		this.uid_num = uid_num;
	}

	public String getEncryptedPid() {
		return encryptedPid;
	}

	public void setEncryptedPid(String encryptedPid) {
		this.encryptedPid = encryptedPid;
	}

	public String getEncSessionKey() {
		return encSessionKey;
	}

	public void setEncSessionKey(String encSessionKey) {
		this.encSessionKey = encSessionKey;
	}

	public String getEncHmac() {
		return encHmac;
	}

	public void setEncHmac(String encHmac) {
		this.encHmac = encHmac;
	}

    public String getDpId() {
        return dpId;
    }

    public void setDpId(String dpId) {
        this.dpId = dpId;
    }

    public String getCrt() {
        return crt;
    }

    public void setCrt(String crt) {
        this.crt = crt;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getUdc() {
        return udc;
    }

    public void setUdc(String udc) {
        this.udc = udc;
    }
    
    public String getRdsVer() {
        return rdsVer;
    }

    public void setRdsVer(String rdsVer) {
        this.rdsVer = rdsVer;
    }

    public String getConsentme() {
        return consentme;
    }

    public void setConsentme(String consentme) {
        this.consentme = consentme;
    }

    public String getRdsId() {
        return rdsId;
    }

    public void setRdsId(String rdsId) {
        this.rdsId = rdsId;
    }

    public String getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(String attemptType) {
        this.attemptType = attemptType;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }
}
