package org.rta.citizen.userregistration.model;

import java.util.List;

import org.rta.citizen.common.model.CustomerDetailsRequestModel;
import org.rta.citizen.common.model.GeolocationModel;
import org.rta.citizen.common.model.LoginModel;
import org.rta.citizen.userregistration.enums.CompanyType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class UserSignupModel {

    private Long userId;
    private String userType;
    private String institutionName;
    private String rocNumber;
    private Long dateOfFirmRegistration;
    private CompanyType companyType;
    private String panNumber;
    private CustomerDetailsRequestModel customerDetails;
    private String officeEmailId;
    private String officeContactNumber;
    private String businessDescription;
    private LoginModel loginDetails;
    private GeolocationModel geolocation;
    private String classOfVehicle;
    private String financierPanNumber;
    private String rtaOfficeCode;
    private String medicalLicenseNumber;
    private Integer numberOfCars;
    private List<String> carsRCNumbers;
    private Integer noOfDrivingInstructors;
    private List<String> drivingInstructorsDLNumbers;
    private Boolean consent;
    private String aadharNumber;
//    private String userTypeLabel;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getRocNumber() {
        return rocNumber;
    }

    public void setRocNumber(String rocNumber) {
        this.rocNumber = rocNumber;
    }

    public Long getDateOfFirmRegistration() {
        return dateOfFirmRegistration;
    }

    public void setDateOfFirmRegistration(Long dateOfFirmRegistration) {
        this.dateOfFirmRegistration = dateOfFirmRegistration;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public CustomerDetailsRequestModel getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetailsRequestModel customerDetails) {
        this.customerDetails = customerDetails;
    }

    public String getOfficeEmailId() {
        return officeEmailId;
    }

    public void setOfficeEmailId(String officeEmailId) {
        this.officeEmailId = officeEmailId;
    }

    public String getOfficeContactNumber() {
        return officeContactNumber;
    }

    public void setOfficeContactNumber(String officeContactNumber) {
        this.officeContactNumber = officeContactNumber;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public LoginModel getLoginDetails() {
        return loginDetails;
    }

    public void setLoginDetails(LoginModel loginDetails) {
        this.loginDetails = loginDetails;
    }

    public GeolocationModel getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeolocationModel geolocation) {
        this.geolocation = geolocation;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

	public String getClassOfVehicle() {
		return classOfVehicle;
	}

	public void setClassOfVehicle(String classOfVehicle) {
		this.classOfVehicle = classOfVehicle;
	}

	public String getFinancierPanNumber() {
		return financierPanNumber;
	}

	public void setFinancierPanNumber(String financierPanNumber) {
		this.financierPanNumber = financierPanNumber;
	}

	public String getRtaOfficeCode() {
		return rtaOfficeCode;
	}

	public void setRtaOfficeCode(String rtaOfficeCode) {
		this.rtaOfficeCode = rtaOfficeCode;
	}

    public String getMedicalLicenseNumber() {
        return medicalLicenseNumber;
    }

    public void setMedicalLicenseNumber(String medicalLicenseNumber) {
        this.medicalLicenseNumber = medicalLicenseNumber;
    }

    public Integer getNumberOfCars() {
        return numberOfCars;
    }

    public void setNumberOfCars(Integer numberOfCars) {
        this.numberOfCars = numberOfCars;
    }

    public List<String> getCarsRCNumbers() {
        return carsRCNumbers;
    }

    public void setCarsRCNumbers(List<String> carsRCNumbers) {
        this.carsRCNumbers = carsRCNumbers;
    }

    public Integer getNoOfDrivingInstructors() {
        return noOfDrivingInstructors;
    }

    public void setNoOfDrivingInstructors(Integer noOfDrivingInstructors) {
        this.noOfDrivingInstructors = noOfDrivingInstructors;
    }

    public List<String> getDrivingInstructorsDLNumbers() {
        return drivingInstructorsDLNumbers;
    }

    public void setDrivingInstructorsDLNumbers(List<String> drivingInstructorsDLNumbers) {
        this.drivingInstructorsDLNumbers = drivingInstructorsDLNumbers;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

/*    public String getUserTypeLabel() {
        return userTypeLabel;
    }

    public void setUserTypeLabel(String userTypeLabel) {
        this.userTypeLabel = userTypeLabel;
    }*/



}
