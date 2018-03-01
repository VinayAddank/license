package org.rta.citizen.common.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class CustomerDetailsRequestModel {

    private String nationality;
    private String ownershipType;
    private String Hand;
    private String Fingure;
    private String base64template;
    private String uid_num;// aadhar
    private String rc_name;
    private String first_name;
    private String last_name;
    private String representedBy;
    private String father_name;
    private String dob;
    private Integer age;
    private String gender;
    private Integer qualification;
    private String qualificationName;
    private boolean physically_challenged;
//    private boolean is_second_vehicle;
    private String uid;// Login userId
    private String passportnum;
    private String passportvalidupto;
    private String vehregrta;
    private String vehregrtacode;
    private String phoneNo;
    private String alternate_mobile;
    private String emailid;
    private String perm_buildingName;
    private String perm_street;
    private String perm_town;
    private String perm_mandal_name;
    private String perm_district_name;
    private String perm_statecode;
    private String perm_nationality;
    private String perm_country;
    private String perm_pincode;
    private String perm_po;
    private String temp_buildingName;
    private String temp_street;
    private String temp_town;
    private String temp_mandal_code;
    private String temp_mandal_name;
    private String temp_district_code;
    private String temp_district_name;
    private String temp_statecode;
    private String temp_statename;
    private String temp_nationality;
    private String temp_country;
    private String temp_pincode;
    private String temp_postoffice;
    private String currentStep;
    private boolean sameAsAddAadhar;
    private String regCategory;
    private String regCategoryName;
    private String rtaOfficeId;
    private String ownershipTypeName;
    private boolean invalidCarriage;
    private String mobileNumber;
    private String permanentPhoneNumber;
    private String status;
    private String surName;
    private String permCity;
    private String type;
    private String tempCity;
    private String mandal;
    private String district;
    private String state;

    private Long vehicleRcId;
    private boolean isInsideAP;
    private boolean isStateMatched;
    private boolean isDistrictMatched;
    private boolean isMandalMatched;
    private String stateMatchedCode;
    private String districtMatchedCode;
    private String mandalMatchedCode;
    private String bloodGroup;
    
    private boolean isDependentAddrs;
    private String addressPerson;
    private String nameOfDependent;
    
    //--for HPA----------
    private boolean applyHPA;
    
    // added for UserRegistration details
//    private String temp_state_name;
    private String perm_state_name;
    private String perm_district_name_display;
    private String perm_mandal_name_display;

    public String getTempCity() {
        return tempCity;
    }

    public void setTempCity(String tempCity) {
        this.tempCity = tempCity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPermCity() {
        return permCity;
    }

    public void setPermCity(String permCity) {
        this.permCity = permCity;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPermanentPhoneNumber() {
        return permanentPhoneNumber;
    }

    public void setPermanentPhoneNumber(String permanentPhoneNumber) {
        this.permanentPhoneNumber = permanentPhoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isInvalidCarriage() {
        return invalidCarriage;
    }

    public void setInvalidCarriage(boolean invalidCarriage) {
        this.invalidCarriage = invalidCarriage;
    }

    public String getRegCategory() {
        return regCategory;
    }

    public void setRegCategory(String regCategory) {
        this.regCategory = regCategory;
    }

    public String getRtaOfficeId() {
        return rtaOfficeId;
    }

    public void setRtaOfficeId(String rtaOfficeId) {
        this.rtaOfficeId = rtaOfficeId;
    }

    public String getOwnershipTypeName() {
        return ownershipTypeName;
    }

    public void setOwnershipTypeName(String ownershipTypeName) {
        this.ownershipTypeName = ownershipTypeName;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public boolean getSameAsAddAadhar() {
        return sameAsAddAadhar;
    }

    public void setSameAsAddAadhar(boolean sameAsAddAadhar) {
        this.sameAsAddAadhar = sameAsAddAadhar;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public String getHand() {
        return Hand;
    }

    public void setHand(String hand) {
        Hand = hand;
    }

    public String getFingure() {
        return Fingure;
    }

    public void setFingure(String fingure) {
        Fingure = fingure;
    }

    public String getBase64template() {
        return base64template;
    }

    public void setBase64template(String base64template) {
        this.base64template = base64template;
    }

    public String getUid_num() {
        return uid_num;
    }

    public void setUid_num(String uid_num) {
        this.uid_num = uid_num;
    }

    public String getRc_name() {
        return rc_name;
    }

    public void setRc_name(String rc_name) {
        this.rc_name = rc_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getRepresentedBy() {
        return representedBy;
    }

    public void setRepresentedBy(String representedBy) {
        this.representedBy = representedBy;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getQualification() {
        return qualification;
    }

    public void setQualification(Integer qualification) {
        this.qualification = qualification;
    }

    public String getQualificationName() {
        return qualificationName;
    }

    public void setQualificationName(String qualificationName) {
        this.qualificationName = qualificationName;
    }

    public boolean getPhysically_challenged() {
        return physically_challenged;
    }

    public void setPhysically_challenged(boolean physically_challenged) {
        this.physically_challenged = physically_challenged;
    }

    /*public boolean getIs_second_vehicle() {
        return is_second_vehicle;
    }

    public void setIs_second_vehicle(boolean is_second_vehicle) {
        this.is_second_vehicle = is_second_vehicle;
    }*/

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassportnum() {
        return passportnum;
    }

    public void setPassportnum(String passportnum) {
        this.passportnum = passportnum;
    }

    public String getPassportvalidupto() {
        return passportvalidupto;
    }

    public void setPassportvalidupto(String passportvalidupto) {
        this.passportvalidupto = passportvalidupto;
    }

    public String getVehregrta() {
        return vehregrta;
    }

    public void setVehregrta(String vehregrta) {
        this.vehregrta = vehregrta;
    }

    public String getVehregrtacode() {
        return vehregrtacode;
    }

    public void setVehregrtacode(String vehregrtacode) {
        this.vehregrtacode = vehregrtacode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAlternate_mobile() {
        return alternate_mobile;
    }

    public void setAlternate_mobile(String alternate_mobile) {
        this.alternate_mobile = alternate_mobile;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPerm_buildingName() {
        return perm_buildingName;
    }

    public void setPerm_buildingName(String perm_buildingName) {
        this.perm_buildingName = perm_buildingName;
    }

    public String getPerm_street() {
        return perm_street;
    }

    public void setPerm_street(String perm_street) {
        this.perm_street = perm_street;
    }

    public String getPerm_town() {
        return perm_town;
    }

    public void setPerm_town(String perm_town) {
        this.perm_town = perm_town;
    }

    public String getPerm_mandal_name() {
        return perm_mandal_name;
    }

    public void setPerm_mandal_name(String perm_mandal_name) {
        this.perm_mandal_name = perm_mandal_name;
    }

    public String getPerm_district_name() {
        return perm_district_name;
    }

    public void setPerm_district_name(String perm_district_name) {
        this.perm_district_name = perm_district_name;
    }

    public String getPerm_statecode() {
        return perm_statecode;
    }

    public void setPerm_statecode(String perm_statecode) {
        this.perm_statecode = perm_statecode;
    }

    public String getPerm_nationality() {
        return perm_nationality;
    }

    public void setPerm_nationality(String perm_nationality) {
        this.perm_nationality = perm_nationality;
    }

    public String getPerm_country() {
        return perm_country;
    }

    public void setPerm_country(String perm_country) {
        this.perm_country = perm_country;
    }

    public String getPerm_pincode() {
        return perm_pincode;
    }

    public void setPerm_pincode(String perm_pincode) {
        this.perm_pincode = perm_pincode;
    }

    public String getPerm_po() {
        return perm_po;
    }

    public void setPerm_po(String perm_postoffice) {
        this.perm_po = perm_postoffice;
    }

    public String getTemp_buildingName() {
        return temp_buildingName;
    }

    public void setTemp_buildingName(String temp_buildingName) {
        this.temp_buildingName = temp_buildingName;
    }

    public String getTemp_street() {
        return temp_street;
    }

    public void setTemp_street(String temp_street) {
        this.temp_street = temp_street;
    }

    public String getTemp_town() {
        return temp_town;
    }

    public void setTemp_town(String temp_town) {
        this.temp_town = temp_town;
    }

    public String getTemp_mandal_name() {
        return temp_mandal_name;
    }

    public void setTemp_mandal_name(String temp_mandal_name) {
        this.temp_mandal_name = temp_mandal_name;
    }

    public String getTemp_district_name() {
        return temp_district_name;
    }

    public void setTemp_district_name(String temp_district_name) {
        this.temp_district_name = temp_district_name;
    }

    public String getTemp_statecode() {
        return temp_statecode;
    }

    public void setTemp_statecode(String temp_statecode) {
        this.temp_statecode = temp_statecode;
    }

    public String getTemp_nationality() {
        return temp_nationality;
    }

    public void setTemp_nationality(String temp_nationality) {
        this.temp_nationality = temp_nationality;
    }

    public String getTemp_country() {
        return temp_country;
    }

    public void setTemp_country(String temp_country) {
        this.temp_country = temp_country;
    }

    public String getTemp_pincode() {
        return temp_pincode;
    }

    public void setTemp_pincode(String temp_pincode) {
        this.temp_pincode = temp_pincode;
    }

    public String getTemp_postoffice() {
        return temp_postoffice;
    }

    public void setTemp_postoffice(String temp_postoffice) {
        this.temp_postoffice = temp_postoffice;
    }

    public String getMandal() {
        return mandal;
    }

    public void setMandal(String mandal) {
        this.mandal = mandal;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getVehicleRcId() {
        return vehicleRcId;
    }

    public void setVehicleRcId(Long vehicleRcId) {
        this.vehicleRcId = vehicleRcId;
    }

    public String getRegCategoryName() {
        return regCategoryName;
    }

    public void setRegCategoryName(String regCategoryName) {
        this.regCategoryName = regCategoryName;
    }

    
    
    public String getTemp_statename() {
        return temp_statename;
    }

    public void setTemp_statename(String temp_statename) {
        this.temp_statename = temp_statename;
    }

    public String getTemp_mandal_code() {
        return temp_mandal_code;
    }

    public void setTemp_mandal_code(String temp_mandal_code) {
        this.temp_mandal_code = temp_mandal_code;
    }

    public String getTemp_district_code() {
        return temp_district_code;
    }

    public void setTemp_district_code(String temp_district_code) {
        this.temp_district_code = temp_district_code;
    }
    
    public boolean isInsideAP() {
        return isInsideAP;
    }

    public void setInsideAP(boolean isInsideAP) {
        this.isInsideAP = isInsideAP;
    }

    public boolean isStateMatched() {
        return isStateMatched;
    }

    public void setStateMatched(boolean isStateMatched) {
        this.isStateMatched = isStateMatched;
    }

    public boolean isDistrictMatched() {
        return isDistrictMatched;
    }

    public void setDistrictMatched(boolean isDistrictMatched) {
        this.isDistrictMatched = isDistrictMatched;
    }

    public boolean isMandalMatched() {
        return isMandalMatched;
    }

    public void setMandalMatched(boolean isMandalMatched) {
        this.isMandalMatched = isMandalMatched;
    }

    public String getStateMatchedCode() {
        return stateMatchedCode;
    }

    public void setStateMatchedCode(String stateMatchedCode) {
        this.stateMatchedCode = stateMatchedCode;
    }

    public String getDistrictMatchedCode() {
        return districtMatchedCode;
    }

    public void setDistrictMatchedCode(String districtMatchedCode) {
        this.districtMatchedCode = districtMatchedCode;
    }

    public String getMandalMatchedCode() {
        return mandalMatchedCode;
    }

    public void setMandalMatchedCode(String mandalMatchedCode) {
        this.mandalMatchedCode = mandalMatchedCode;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public boolean isDependentAddrs() {
        return isDependentAddrs;
    }

    public void setDependentAddrs(boolean isDependentAddrs) {
        this.isDependentAddrs = isDependentAddrs;
    }

    public String getAddressPerson() {
        return addressPerson;
    }

    public void setAddressPerson(String addressPerson) {
        this.addressPerson = addressPerson;
    }
    

    public String getNameOfDependent() {
		return nameOfDependent;
	}

	public void setNameOfDependent(String nameOfDependent) {
		this.nameOfDependent = nameOfDependent;
	}
	
	public boolean isApplyHPA() {
        return applyHPA;
    }

    public void setApplyHPA(boolean applyHPA) {
        this.applyHPA = applyHPA;
    }

/*    public String getTemp_state_name() {
        return temp_state_name;
    }

    public void setTemp_state_name(String temp_state_name) {
        this.temp_state_name = temp_state_name;
    }*/

    public String getPerm_state_name() {
        return perm_state_name;
    }

    public void setPerm_state_name(String perm_state_name) {
        this.perm_state_name = perm_state_name;
    }

    public String getPerm_district_name_display() {
        return perm_district_name_display;
    }

    public void setPerm_district_name_display(String perm_district_name_display) {
        this.perm_district_name_display = perm_district_name_display;
    }

    public String getPerm_mandal_name_display() {
        return perm_mandal_name_display;
    }

    public void setPerm_mandal_name_display(String perm_mandal_name_display) {
        this.perm_mandal_name_display = perm_mandal_name_display;
    }

    @Override
    public String toString() {
        return "CustomerDetailsRequestModel [nationality=" + nationality + ", ownershipType=" + ownershipType
                + ", Hand=" + Hand + ", Fingure=" + Fingure + ", base64template=" + base64template + ", uid_num="
                + uid_num + ", rc_name=" + rc_name + ", first_name=" + first_name + ", last_name=" + last_name
                + ", representedBy=" + representedBy + ", father_name=" + father_name + ", dob=" + dob + ", gender="
                + gender + ", qualification=" + qualification + ", qualificationName=" + qualificationName
                + ", physically_challenged=" + physically_challenged + ", uid=" + uid + ", passportnum=" + passportnum
                + ", passportvalidupto=" + passportvalidupto + ", vehregrta=" + vehregrta + ", vehregrtacode="
                + vehregrtacode + ", phoneNo=" + phoneNo + ", alternate_mobile=" + alternate_mobile + ", emailid="
                + emailid + ", perm_buildingName=" + perm_buildingName + ", perm_street=" + perm_street + ", perm_town="
                + perm_town + ", perm_mandal_name=" + perm_mandal_name + ", perm_district_name=" + perm_district_name
                + ", perm_statecode=" + perm_statecode + ", perm_nationality=" + perm_nationality + ", perm_country="
                + perm_country + ", perm_pincode=" + perm_pincode + ", perm_po=" + perm_po + ", temp_buildingName="
                + temp_buildingName + ", temp_street=" + temp_street + ", temp_town=" + temp_town
                + ", temp_mandal_code=" + temp_mandal_code + ", temp_mandal_name=" + temp_mandal_name
                + ", temp_district_code=" + temp_district_code + ", temp_district_name=" + temp_district_name
                + ", temp_statecode=" + temp_statecode + ", temp_statename=" + temp_statename + ", temp_nationality="
                + temp_nationality + ", temp_country=" + temp_country + ", temp_pincode=" + temp_pincode
                + ", currentStep=" + currentStep + ", sameAsAddAadhar=" + sameAsAddAadhar + ", regCategory="
                + regCategory + ", regCategoryName=" + regCategoryName + ", rtaOfficeId=" + rtaOfficeId
                + ", ownershipTypeName=" + ownershipTypeName + ", invalidCarriage=" + invalidCarriage
                + ", mobileNumber=" + mobileNumber + ", permanentPhoneNumber=" + permanentPhoneNumber + ", status="
                + status + ", surName=" + surName + ", permCity=" + permCity + ", type=" + type + ", tempCity="
                + tempCity + ", mandal=" + mandal + ", district=" + district + ", state=" + state + ", vehicleRcId="
                + vehicleRcId + ", isInsideAP=" + isInsideAP + ", isStateMatched=" + isStateMatched
                + ", isDistrictMatched=" + isDistrictMatched + ", isMandalMatched=" + isMandalMatched
                + ", stateMatchedCode=" + stateMatchedCode + ", districtMatchedCode=" + districtMatchedCode
                + ", mandalMatchedCode=" + mandalMatchedCode + ", bloodGroup=" + bloodGroup + ", isDependentAddrs="
                + isDependentAddrs + ", addressPerson=" + addressPerson + ", nameOfDependent=" + nameOfDependent
                + ", applyHPA=" + applyHPA + "]";
    }

}
