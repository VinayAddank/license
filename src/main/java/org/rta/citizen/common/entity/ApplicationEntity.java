package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author arun.verma
 *
 */
@Entity
@Table(name = "application")
public class ApplicationEntity extends BaseEntity {

    private static final long serialVersionUID = -1612246956883287976L;

    @Id
    @Column(name = "application_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_seq")
    @SequenceGenerator(name = "application_seq", sequenceName = "application_seq", allocationSize = 1)
    private Long applicationId;
    
    @Column(name = "application_number")
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "session_id")
    private UserSessionEntity loginHistory;
    
    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "execution_id")
    private String executionId;

    @Column(name = "process_id")
    private String processId;
    
    @Column(name = "is_authenticated")
    private Boolean isAuthenticated;

    @Column(name = "rta_office_code")
    private String rtaOfficeCode;    

    @Column(name = "iteration", columnDefinition = "int default 1")
    private Integer iteration;

    @Column(name = "is_sync_with_master", columnDefinition = "boolean default false")
    private boolean isSyncWithMaster;

    @Column(name = "applicant_dob")
    private String applicantDob;
    
    @Column(name = "service_category", length = 20)
    private String serviceCategory;
    
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public UserSessionEntity getLoginHistory() {
        return loginHistory;
    }

    public void setLoginHistory(UserSessionEntity loginHistory) {
        this.loginHistory = loginHistory;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    /**
     * is aadhar authenticated or not
     * @return
     */
    public Boolean getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public String getRtaOfficeCode() {
        return rtaOfficeCode;
    }

    public void setRtaOfficeCode(String rtaOfficeCode) {
        this.rtaOfficeCode = rtaOfficeCode;
    }

    public Integer getIteration() {
        return iteration;
    }

    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    public boolean isSyncWithMaster() {
        return isSyncWithMaster;
    }

    public void setSyncWithMaster(boolean isSyncWithMaster) {
        this.isSyncWithMaster = isSyncWithMaster;
    }

    public String getApplicantDob() {
        return applicantDob;
    }

    public void setApplicantDob(String applicantDob) {
        this.applicantDob = applicantDob;
    }

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}
    
}
