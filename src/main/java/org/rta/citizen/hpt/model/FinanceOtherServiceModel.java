/**
 * 
 */
package org.rta.citizen.hpt.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@JsonInclude(Include.NON_NULL)
public class FinanceOtherServiceModel {

    private String appNo;
    private String financierName;
    private Long financierId;
    private String prNumber;
    private String serviceCode;
    private Integer serviceType;
    private Long agreementDate;
    private Boolean isTerminated;
    private Boolean isDeclared;

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public String getFinancierName() {
        return financierName;
    }

    public void setFinancierName(String financierName) {
        this.financierName = financierName;
    }

    public Long getFinancierId() {
        return financierId;
    }

    public void setFinancierId(Long financierId) {
        this.financierId = financierId;
    }

    public String getPrNumber() {
        return prNumber;
    }

    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Long getAgreementDate() {
        return agreementDate;
    }

    public void setAgreementDate(Long agreementDate) {
        this.agreementDate = agreementDate;
    }

    public Boolean getIsTerminated() {
        return isTerminated;
    }

    public void setIsTerminated(Boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    public Boolean getIsDeclared() {
        return isDeclared;
    }

    public void setIsDeclared(Boolean isDeclared) {
        this.isDeclared = isDeclared;
    }

    @Override
    public String toString() {
        return "FinanceOtherServiceModel [appNo=" + appNo + ", financierName=" + financierName + ", financierId="
                + financierId + ", prNumber=" + prNumber + ", serviceCode=" + serviceCode + ", serviceType="
                + serviceType + ", agreementDate=" + agreementDate + ", isTerminated=" + isTerminated + ", isDeclared="
                + isDeclared + "]";
    }

}
