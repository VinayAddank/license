/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.enums.PRType;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.UserType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author arun.verma
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class ApplicationModel {

    private Long vehicleRcId;
    private Status status;
    private Integer currentStep;
    private Status processStatus;
    private Status trStatus;
    private Status prStatus;
    private String trNumber;
    private String prNumber;
    private Long trIssueTime;
    private Long prIssueTime;
    private Map<UserType, Status> appStatus;
    private RegistrationCategoryModel registrationCategory;
    private VehicleBaseModel vehicleModel;
    private CustomerDetailsRequestModel customerDetails;
    private BillingDetailsModel billingDetails;
    private CommentModel comment;
    private Integer iteration;
    private Long applicationDate;
    private Integer compStatus;
    private boolean aadharVerified;
    private boolean docUploadConsent;
    private String aadharNumber;
    private String userId;
    private boolean paymentAllow;
    private PRType prType;
    private Boolean canBeApproved;
    private RTAOfficeModel rtaOffice;
    private SuspendedRCNumberModel suspensionDetails;
    private Long prValidUpto;
    private FcDetailsModel fitnessCertificateDetails;
    private long greenTaxValidTo;
    private String specialNumber;
    private boolean isIncompleteData;
    private String incompleteReason;
    
    public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public boolean isPaymentAllow() {
		return paymentAllow;
	}
	public void setPaymentAllow(boolean paymentAllow) {
		this.paymentAllow = paymentAllow;
	}
	public boolean isDocUploadConsent() {
		return docUploadConsent;
	}
	public void setDocUploadConsent(boolean docUploadConsent) {
		this.docUploadConsent = docUploadConsent;
	}
	public Long getVehicleRcId() {
        return vehicleRcId;
    }
    public void setVehicleRcId(Long vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Integer getCurrentStep() {
        return currentStep;
    }
    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }
    /*public Integer getProcessStatus() {
        return processStatus;
    }
    public void setProcessStatus(Integer processStatus) {
        this.processStatus = processStatus;
    }*/
   public Status getTrStatus() {
        return trStatus;
    }
    public void setTrStatus(Status trStatus) {
        this.trStatus = trStatus;
    }
    public Status getPrStatus() {
        return prStatus;
    }
    public void setPrStatus(Status prStatus) {
        this.prStatus = prStatus;
    }
    public String getTrNumber() {
        return trNumber;
    }
    public void setTrNumber(String trNumber) {
        this.trNumber = trNumber;
    }
    public String getPrNumber() {
        return prNumber;
    }
    public void setPrNumber(String prNumber) {
        this.prNumber = prNumber;
    }
    public Long getTrIssueTime() {
        return trIssueTime;
    }
    public void setTrIssueTime(Long trIssueTime) {
        this.trIssueTime = trIssueTime;
    }
    public Long getPrIssueTime() {
        return prIssueTime;
    }
    public void setPrIssueTime(Long prIssueTime) {
        this.prIssueTime = prIssueTime;
    }
    public Map<UserType, Status> getAppStatus() {
        return appStatus;
    }
    public void setAppStatus(Map<UserType, Status> appStatus) {
        this.appStatus = appStatus;
    }
    public RegistrationCategoryModel getRegistrationCategory() {
        return registrationCategory;
    }
    public void setRegistrationCategory(RegistrationCategoryModel registrationCategory) {
        this.registrationCategory = registrationCategory;
    }
    
    @JsonProperty("vehicle")
    public VehicleBaseModel getVehicleModel() {
        return vehicleModel;
    }
    public void setVehicleModel(VehicleBaseModel vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    
    @JsonProperty("customer")
    public CustomerDetailsRequestModel getCustomerDetails() {
        return customerDetails;
    }
    public void setCustomerDetails(CustomerDetailsRequestModel customerDetails) {
        this.customerDetails = customerDetails;
    }
    public BillingDetailsModel getBillingDetails() {
        return billingDetails;
    }
    public void setBillingDetailsModel(BillingDetailsModel billingDetails) {
        this.billingDetails = billingDetails;
    }
    public CommentModel getComment() {
        return comment;
    }
    public void setComment(CommentModel comment) {
        this.comment = comment;
    }
    public Integer getIteration() {
        return iteration;
    }
    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    public Long getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Long applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getCompStatus() {
        return compStatus;
    }

    public void setCompStatus(Integer compStatus) {
        this.compStatus = compStatus;
    }

    public void setBillingDetails(BillingDetailsModel billingDetails) {
        this.billingDetails = billingDetails;
    }
	public boolean isAadharVerified() {
		return aadharVerified;
	}
	public void setAadharVerified(boolean aadharVerified) {
		this.aadharVerified = aadharVerified;
	}
	public String getAadharNumber() {
		return aadharNumber;
	}
	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}
    public PRType getPrType() {
        return prType;
    }
    public void setPrType(PRType prType) {
        this.prType = prType;
    }
    public Boolean getCanBeApproved() {
        return canBeApproved;
    }
    public void setCanBeApproved(Boolean canBeApproved) {
        this.canBeApproved = canBeApproved;
    }
    public RTAOfficeModel getRtaOffice() {
        return rtaOffice;
    }
    public void setRtaOffice(RTAOfficeModel rtaOffice) {
        this.rtaOffice = rtaOffice;
    }
    public SuspendedRCNumberModel getSuspensionDetails() {
        return suspensionDetails;
    }
    public void setSuspensionDetails(SuspendedRCNumberModel suspensionDetails) {
        this.suspensionDetails = suspensionDetails;
    }
    public Long getPrValidUpto() {
        return prValidUpto;
    }
    public void setPrValidUpto(Long prValidUpto) {
        this.prValidUpto = prValidUpto;
    }
    public FcDetailsModel getFitnessCertificateDetails() {
        return fitnessCertificateDetails;
    }
    public void setFitnessCertificateDetails(FcDetailsModel fitnessCertificateDetails) {
        this.fitnessCertificateDetails = fitnessCertificateDetails;
    }
	public long getGreenTaxValidTo() {
		return greenTaxValidTo;
	}
	public void setGreenTaxValidTo(long greenTaxValidTo) {
		this.greenTaxValidTo = greenTaxValidTo;
	}
	public Status getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(Status processStatus) {
		this.processStatus = processStatus;
	}
	public String getSpecialNumber() {
		return specialNumber;
	}
	public void setSpecialNumber(String specialNumber) {
		this.specialNumber = specialNumber;
	}
	public boolean isIncompleteData() {
		return isIncompleteData;
	}
	public void setIncompleteData(boolean isIncompleteData) {
		this.isIncompleteData = isIncompleteData;
	}
	public String getIncompleteReason() {
		return incompleteReason;
	}
	public void setIncompleteReason(String incompleteReason) {
		this.incompleteReason = incompleteReason;
	}
	
}
