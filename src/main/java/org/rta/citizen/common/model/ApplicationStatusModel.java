/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.TheftIntSusType;
import org.rta.citizen.common.model.activiti.RtaTaskInfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class ApplicationStatusModel {

    private String applicationType;
    private String serviceCode;
    private List<RtaTaskInfo> activitiTasks;
    private Status overAllStatus;
    private Long submittedOn;
    private Status sellerStatus;
    private Long sellerActionDate;
    private String sellerRemark;
    private Status buyerStatus;
    private Long buyerActionDate;
    private String buyerRemark;
    private Status financierStatus;
    private Long financierActionDate;
    private String financierRemark;
    private Status ccoStatus;
    private Long ccoActionDate;
    private Long ccoActionTime;
    private String ccoRemark;
    private Status mviStatus;
    private Long mviActionDate;
    private Long mviActionTime;
    private String mviRemark;
    private Status aoStatus;
    private Long aoActionDate;
    private Long aoActionTime;
    private String aoRemark;
    private Status rtoStatus;
    private Long rtoActionDate;
    private Long rtoActionTime;
    private String rtoRemark;
    private Status dtcStatus;
    private Long dtcActionDate;
    private String dtcRemark;
    private Status examStatus;
    private Long examActionDate;
    private Long examActionTime;
    private String examRemark;
    private RegistrationCategoryModel regCategory;
    private TheftIntSusType theftStatus;
    private String covsStatus;
    private String permitOption;
    private Status citizenStatus;
    private Long citizenActionDate;
    private String citizenRemark;
    private Boolean isDownloadFitness;
    private String permitType;
    
    public String getApplicationType() {
		return applicationType;
	}


	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}


	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public List<RtaTaskInfo> getActivitiTasks() {
		return activitiTasks;
	}


	public void setActivitiTasks(List<RtaTaskInfo> activitiTasks) {
		this.activitiTasks = activitiTasks;
	}


	public Status getOverAllStatus() {
		return overAllStatus;
	}


	public void setOverAllStatus(Status overAllStatus) {
		this.overAllStatus = overAllStatus;
	}


	public Long getSubmittedOn() {
		return submittedOn;
	}


	public void setSubmittedOn(Long submittedOn) {
		this.submittedOn = submittedOn;
	}


	public Status getSellerStatus() {
		return sellerStatus;
	}


	public void setSellerStatus(Status sellerStatus) {
		this.sellerStatus = sellerStatus;
	}


	public Long getSellerActionDate() {
		return sellerActionDate;
	}


	public void setSellerActionDate(Long sellerActionDate) {
		this.sellerActionDate = sellerActionDate;
	}


	public String getSellerRemark() {
		return sellerRemark;
	}


	public void setSellerRemark(String sellerRemark) {
		this.sellerRemark = sellerRemark;
	}


	public Status getBuyerStatus() {
		return buyerStatus;
	}


	public void setBuyerStatus(Status buyerStatus) {
		this.buyerStatus = buyerStatus;
	}


	public Long getBuyerActionDate() {
		return buyerActionDate;
	}


	public void setBuyerActionDate(Long buyerActionDate) {
		this.buyerActionDate = buyerActionDate;
	}


	public String getBuyerRemark() {
		return buyerRemark;
	}


	public void setBuyerRemark(String buyerRemark) {
		this.buyerRemark = buyerRemark;
	}


	public Status getFinancierStatus() {
		return financierStatus;
	}


	public void setFinancierStatus(Status financierStatus) {
		this.financierStatus = financierStatus;
	}


	public Long getFinancierActionDate() {
		return financierActionDate;
	}


	public void setFinancierActionDate(Long financierActionDate) {
		this.financierActionDate = financierActionDate;
	}


	public String getFinancierRemark() {
		return financierRemark;
	}


	public void setFinancierRemark(String financierRemark) {
		this.financierRemark = financierRemark;
	}


	public Status getCcoStatus() {
		return ccoStatus;
	}


	public void setCcoStatus(Status ccoStatus) {
		this.ccoStatus = ccoStatus;
	}


	public Long getCcoActionDate() {
		return ccoActionDate;
	}


	public void setCcoActionDate(Long ccoActionDate) {
		this.ccoActionDate = ccoActionDate;
	}


	public Long getCcoActionTime() {
		return ccoActionTime;
	}


	public void setCcoActionTime(Long ccoActionTime) {
		this.ccoActionTime = ccoActionTime;
	}


	public String getCcoRemark() {
		return ccoRemark;
	}


	public void setCcoRemark(String ccoRemark) {
		this.ccoRemark = ccoRemark;
	}


	public Status getMviStatus() {
		return mviStatus;
	}


	public void setMviStatus(Status mviStatus) {
		this.mviStatus = mviStatus;
	}


	public Long getMviActionDate() {
		return mviActionDate;
	}


	public void setMviActionDate(Long mviActionDate) {
		this.mviActionDate = mviActionDate;
	}


	public Long getMviActionTime() {
		return mviActionTime;
	}


	public void setMviActionTime(Long mviActionTime) {
		this.mviActionTime = mviActionTime;
	}


	public String getMviRemark() {
		return mviRemark;
	}


	public void setMviRemark(String mviRemark) {
		this.mviRemark = mviRemark;
	}


	public Status getAoStatus() {
		return aoStatus;
	}


	public void setAoStatus(Status aoStatus) {
		this.aoStatus = aoStatus;
	}


	public Long getAoActionDate() {
		return aoActionDate;
	}


	public void setAoActionDate(Long aoActionDate) {
		this.aoActionDate = aoActionDate;
	}


	public Long getAoActionTime() {
		return aoActionTime;
	}


	public void setAoActionTime(Long aoActionTime) {
		this.aoActionTime = aoActionTime;
	}


	public String getAoRemark() {
		return aoRemark;
	}


	public void setAoRemark(String aoRemark) {
		this.aoRemark = aoRemark;
	}


	public Status getRtoStatus() {
		return rtoStatus;
	}


	public void setRtoStatus(Status rtoStatus) {
		this.rtoStatus = rtoStatus;
	}


	public Long getRtoActionDate() {
		return rtoActionDate;
	}


	public void setRtoActionDate(Long rtoActionDate) {
		this.rtoActionDate = rtoActionDate;
	}


	public Long getRtoActionTime() {
		return rtoActionTime;
	}


	public void setRtoActionTime(Long rtoActionTime) {
		this.rtoActionTime = rtoActionTime;
	}


	public String getRtoRemark() {
		return rtoRemark;
	}


	public void setRtoRemark(String rtoRemark) {
		this.rtoRemark = rtoRemark;
	}


	public Status getDtcStatus() {
		return dtcStatus;
	}


	public void setDtcStatus(Status dtcStatus) {
		this.dtcStatus = dtcStatus;
	}


	public Long getDtcActionDate() {
		return dtcActionDate;
	}


	public void setDtcActionDate(Long dtcActionDate) {
		this.dtcActionDate = dtcActionDate;
	}


	public String getDtcRemark() {
		return dtcRemark;
	}


	public void setDtcRemark(String dtcRemark) {
		this.dtcRemark = dtcRemark;
	}


	public RegistrationCategoryModel getRegCategory() {
		return regCategory;
	}


	public void setRegCategory(RegistrationCategoryModel regCategory) {
		this.regCategory = regCategory;
	}

	public Status getExamStatus() {
        return examStatus;
    }


    public void setExamStatus(Status examStatus) {
        this.examStatus = examStatus;
    }


    public Long getExamActionDate() {
        return examActionDate;
    }


    public void setExamActionDate(Long examActionDate) {
        this.examActionDate = examActionDate;
    }


    public Long getExamActionTime() {
        return examActionTime;
    }


    public void setExamActionTime(Long examActionTime) {
        this.examActionTime = examActionTime;
    }


    public String getExamRemark() {
        return examRemark;
    }


    public void setExamRemark(String examRemark) {
        this.examRemark = examRemark;
    }


    public TheftIntSusType getTheftStatus() {
        return theftStatus;
    }


    public void setTheftStatus(TheftIntSusType theftStatus) {
        this.theftStatus = theftStatus;
    }


    public String getCovsStatus() {
		return covsStatus;
	}


	public void setCovsStatus(String covsStatus) {
		this.covsStatus = covsStatus;
	}

	public String getPermitOption() {
		return permitOption;
	}


	public void setPermitOption(String permitOption) {
		this.permitOption = permitOption;
	}


	public Status getCitizenStatus() {
		return citizenStatus;
	}


	public void setCitizenStatus(Status citizenStatus) {
		this.citizenStatus = citizenStatus;
	}


	public Long getCitizenActionDate() {
		return citizenActionDate;
	}


	public void setCitizenActionDate(Long citizenActionDate) {
		this.citizenActionDate = citizenActionDate;
	}


	public String getCitizenRemark() {
		return citizenRemark;
	}


	public void setCitizenRemark(String citizenRemark) {
		this.citizenRemark = citizenRemark;
	}


	public Boolean getIsDownloadFitness() {
		return isDownloadFitness;
	}


	public void setIsDownloadFitness(Boolean isDownloadFitness) {
		this.isDownloadFitness = isDownloadFitness;
	}


	public String getPermitType() {
		return permitType;
	}


	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}
	
}
