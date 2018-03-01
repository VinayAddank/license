package org.rta.citizen.licence.model;

import java.util.Date;
import java.util.List;

import org.rta.citizen.common.model.ChallanDetailsModel;
import org.rta.citizen.common.model.CrimeDetailsModel;

public class LicenceDetailsModel {

	private String application_id;
	private String llr_no;
	private String name;
	private String coName;
	private String dob;
	private String bloodGroup;
	private String nationality;
	private String pres_addr_door_no;
	private String pres_addr_street;
	private String pres_addr_town;
	private Long pres_addr_mandal_id;
	private Long pres_addr_district_id;
	private Long pres_addr_state_id;
	private String pres_addr_country_id;
	private String pres_addr_pin_code;
	private Date issuedDate;
	private String issuedBy;
	private Date covValidity;
	private String refNo;
	private String refLA;
	private String dl_no;
	private List<ChallanDetailsModel> challanDetailsList;
	private List<CrimeDetailsModel> crimeDetailsList;

	public String getApplication_id() {
		return application_id;
	}

	public void setApplication_id(String application_id) {
		this.application_id = application_id;
	}

	public String getLlr_no() {
		return llr_no;
	}

	public void setLlr_no(String llr_no) {
		this.llr_no = llr_no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoName() {
		return coName;
	}

	public void setCoName(String coName) {
		this.coName = coName;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getPres_addr_door_no() {
		return pres_addr_door_no;
	}

	public void setPres_addr_door_no(String pres_addr_door_no) {
		this.pres_addr_door_no = pres_addr_door_no;
	}

	public String getPres_addr_street() {
		return pres_addr_street;
	}

	public void setPres_addr_street(String pres_addr_street) {
		this.pres_addr_street = pres_addr_street;
	}

	public String getPres_addr_town() {
		return pres_addr_town;
	}

	public void setPres_addr_town(String pres_addr_town) {
		this.pres_addr_town = pres_addr_town;
	}

	public String getPres_addr_pin_code() {
		return pres_addr_pin_code;
	}

	public void setPres_addr_pin_code(String pres_addr_pin_code) {
		this.pres_addr_pin_code = pres_addr_pin_code;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public List<ChallanDetailsModel> getChallanDetailsList() {
		return challanDetailsList;
	}

	public void setChallanDetailsList(List<ChallanDetailsModel> challanDetailsList) {
		this.challanDetailsList = challanDetailsList;
	}

	public List<CrimeDetailsModel> getCrimeDetailsList() {
		return crimeDetailsList;
	}

	public void setCrimeDetailsList(List<CrimeDetailsModel> crimeDetailsList) {
		this.crimeDetailsList = crimeDetailsList;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public Long getPres_addr_mandal_id() {
		return pres_addr_mandal_id;
	}

	public void setPres_addr_mandal_id(Long pres_addr_mandal_id) {
		this.pres_addr_mandal_id = pres_addr_mandal_id;
	}

	public Long getPres_addr_district_id() {
		return pres_addr_district_id;
	}

	public void setPres_addr_district_id(Long pres_addr_district_id) {
		this.pres_addr_district_id = pres_addr_district_id;
	}

	public Long getPres_addr_state_id() {
		return pres_addr_state_id;
	}

	public void setPres_addr_state_id(Long pres_addr_state_id) {
		this.pres_addr_state_id = pres_addr_state_id;
	}

	public String getPres_addr_country_id() {
		return pres_addr_country_id;
	}

	public void setPres_addr_country_id(String pres_addr_country_id) {
		this.pres_addr_country_id = pres_addr_country_id;
	}

	public Date getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public Date getCovValidity() {
		return covValidity;
	}

	public void setCovValidity(Date covValidity) {
		this.covValidity = covValidity;
	}

	public String getRefLA() {
		return refLA;
	}

	public void setRefLA(String refLA) {
		this.refLA = refLA;
	}

	public String getDl_no() {
		return dl_no;
	}

	public void setDl_no(String dl_no) {
		this.dl_no = dl_no;
	}

}
