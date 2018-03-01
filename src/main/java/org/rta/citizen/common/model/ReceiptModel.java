/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.model.payment.FeeModel;
import org.rta.citizen.slotbooking.model.SlotModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class ReceiptModel {

    private String serviceType;
    private Long currDateTime;
    private String appNumber;
    private String tokenNumber;
    private ApplicantModel applicantDetails;
    private FeeModel feeDetails;
    private RTAOfficeModel rtaOfficeModel;
    private List<SlotModel> slotModel;

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Long getCurrDateTime() {
        return currDateTime;
    }

    public void setCurrDateTime(Long currDateTime) {
        this.currDateTime = currDateTime;
    }

    public String getAppNumber() {
        return appNumber;
    }

    public void setAppNumber(String appNumber) {
        this.appNumber = appNumber;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public ApplicantModel getApplicantDetails() {
        return applicantDetails;
    }

    public void setApplicantDetails(ApplicantModel applicantDetails) {
        this.applicantDetails = applicantDetails;
    }

    public FeeModel getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(FeeModel feeDetails) {
        this.feeDetails = feeDetails;
    }

    public RTAOfficeModel getRtaOfficeModel() {
        return rtaOfficeModel;
    }

    public void setRtaOfficeModel(RTAOfficeModel rtaOfficeModel) {
        this.rtaOfficeModel = rtaOfficeModel;
    }

    public List<SlotModel> getSlotModel() {
        return slotModel;
    }

    public void setSlotModel(List<SlotModel> slotModel) {
        this.slotModel = slotModel;
    }

}
