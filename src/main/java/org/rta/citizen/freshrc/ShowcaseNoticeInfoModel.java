package org.rta.citizen.freshrc;


import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.model.BaseModel;
import org.rta.citizen.common.model.RTAOfficeModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ShowcaseNoticeInfoModel extends BaseModel {

    private static final long serialVersionUID = -4358984802070052598L;
    private RTAOfficeModel rtaOffice;
    private String frfNumber;
    private Long generationDate;
    private String currentOwnerName;
    private String financerName;
    private String registrationNumber;
    private AddressModel financerAdress;
    private AddressModel ownerAdress;
    private String ccoSignature;
    
    public RTAOfficeModel getRtaOffice() {
        return rtaOffice;
    }
    public void setRtaOffice(RTAOfficeModel rtaOffice) {
        this.rtaOffice = rtaOffice;
    }
    public String getFrfNumber() {
        return frfNumber;
    }
    public void setFrfNumber(String frfNumber) {
        this.frfNumber = frfNumber;
    }
    public Long getGenerationDate() {
        return generationDate;
    }
    public void setGenerationDate(Long generationDate) {
        this.generationDate = generationDate;
    }
    public String getCurrentOwnerName() {
        return currentOwnerName;
    }
    public void setCurrentOwnerName(String currentOwnerName) {
        this.currentOwnerName = currentOwnerName;
    }
    public String getFinancerName() {
        return financerName;
    }
    public void setFinancerName(String financerName) {
        this.financerName = financerName;
    }
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
	public AddressModel getFinancerAdress() {
		return financerAdress;
	}
	public void setFinancerAdress(AddressModel financerAdress) {
		this.financerAdress = financerAdress;
	}
	public AddressModel getOwnerAdress() {
		return ownerAdress;
	}
	public void setOwnerAdress(AddressModel ownerAdress) {
		this.ownerAdress = ownerAdress;
	}
	public String getCcoSignature() {
		return ccoSignature;
	}
	public void setCcoSignature(String ccoSignature) {
		this.ccoSignature = ccoSignature;
	}
	
}
