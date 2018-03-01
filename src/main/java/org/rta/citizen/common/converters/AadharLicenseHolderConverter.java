package org.rta.citizen.common.converters;

import org.rta.citizen.common.model.AadharModel;
import org.rta.citizen.common.model.MandalModel;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.licence.model.updated.LicenseHolderDtlsModel;
import org.springframework.stereotype.Component;

@Component
public class AadharLicenseHolderConverter {

	public LicenseHolderDtlsModel convertToLicenseHolderDtlsModel(AadharModel source) {

		LicenseHolderDtlsModel licenseHolderDtlsModel = new LicenseHolderDtlsModel();
		String[] nameArr = source.getName().trim().split(" ", 2);
		licenseHolderDtlsModel.setAadhaarNo(String.valueOf(source.getUid()));
		licenseHolderDtlsModel.setDateOfBirth(DateUtil.getDatefromString(source.getDob()));
		licenseHolderDtlsModel.setDisplayName(source.getName());
		licenseHolderDtlsModel.setFirstName(nameArr[0]);
		licenseHolderDtlsModel.setGender(source.getGender());
		licenseHolderDtlsModel.setGuardianName(source.getCo());
		licenseHolderDtlsModel.setIsActive("Y");
		licenseHolderDtlsModel.setIsAdharVerify("Y");
		licenseHolderDtlsModel.setLastName(nameArr[1]);
		licenseHolderDtlsModel.setMandalDetails(new MandalModel());
		licenseHolderDtlsModel.getMandalDetails().setName(source.getMandal_name());
		licenseHolderDtlsModel.setNationality("Indian");
		licenseHolderDtlsModel.setPermAddrCountry("India");
		licenseHolderDtlsModel.setPermAddrDistrict(source.getDistrict_name());
		licenseHolderDtlsModel.setPermAddrDoorNo(source.getHouse());
		licenseHolderDtlsModel.setPermAddrMandal(source.getMandal_name());
		licenseHolderDtlsModel.setPermAddrPinCode(source.getPincode());
		licenseHolderDtlsModel.setPermAddrState(source.getStatecode());
		licenseHolderDtlsModel.setPermAddrStreet(source.getStreet());
		licenseHolderDtlsModel.setPermAddrTown(source.getVillage_name());
		return licenseHolderDtlsModel;
	}
}
