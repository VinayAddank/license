package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *	@Author sohan.maurya created on Nov 10, 2016.
 */
@XmlRootElement
public class FcDetailsModel {


    private String fcNumber;
    private String rtaOfficeName;
    private Long issueDate;
    private Long expiryDate;
    private Boolean status;
    private String mviName;
    private String signatureFilePath;
    private Boolean suspended;

    public String getFcNumber() {
        return fcNumber;
    }

    public void setFcNumber(String fcNumber) {
        this.fcNumber = fcNumber;
    }

    public String getRtaOfficeName() {
        return rtaOfficeName;
    }

    public void setRtaOfficeName(String rtaOfficeName) {
        this.rtaOfficeName = rtaOfficeName;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMviName() {
        return mviName;
    }

    public void setMviName(String mviName) {
        this.mviName = mviName;
    }

    public String getSignatureFilePath() {
        return signatureFilePath;
    }

    public void setSignatureFilePath(String signatureFilePath) {
        this.signatureFilePath = signatureFilePath;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }
    
}
