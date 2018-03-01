package org.rta.citizen.licence.model.updated;

import java.util.List;

public class LicenseHolderPermitDetails {

	private String applicationNo;
	private LicenseHolderDtlsModel licenseHolderDetails;
	private List<LearnersPermitDtlModel> learnersPermitDetailsList;
	private List<DriversLicenceDetailsModel> driversPermitDetailsList;

	public String getApplicationNo() {
		return applicationNo;
	}

	public void setApplicationNo(String applicationNo) {
		this.applicationNo = applicationNo;
	}

	public LicenseHolderDtlsModel getLicenseHolderDetails() {
		return licenseHolderDetails;
	}

	public void setLicenseHolderDetails(LicenseHolderDtlsModel licenseHolderDetails) {
		this.licenseHolderDetails = licenseHolderDetails;
	}

	public List<LearnersPermitDtlModel> getLearnersPermitDetailsList() {
		return learnersPermitDetailsList;
	}

	public void setLearnersPermitDetailsList(List<LearnersPermitDtlModel> learnersPermitDetailsList) {
		this.learnersPermitDetailsList = learnersPermitDetailsList;
	}

	public List<DriversLicenceDetailsModel> getDriversPermitDetailsList() {
		return driversPermitDetailsList;
	}

	public void setDriversPermitDetailsList(List<DriversLicenceDetailsModel> driversPermitDetailsList) {
		this.driversPermitDetailsList = driversPermitDetailsList;
	}

}
