package org.rta.citizen.common.converters;

import java.util.Collection;
import java.util.stream.Collectors;

import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.entity.updated.LicensePermitDetailsEntity;
import org.rta.citizen.licence.model.updated.LicensePermitDetailsModel;
import org.springframework.stereotype.Component;

@Component
public class LicenseDetailsConverter implements BaseConverter<LicensePermitDetailsEntity, LicensePermitDetailsModel>{

	@Override
	public LicensePermitDetailsModel convertToModel(LicensePermitDetailsEntity source) {
		
		if(ObjectsUtil.isNull(source)){
			return null;
		}
		LicensePermitDetailsModel model = new LicensePermitDetailsModel();
			
		model.setLicenseNumber(source.getLicenseNumber() );
		model.setLicenseType(source.getLicenseType() );
		model.setVehicleClassCode(source.getVehicleClassCode() );
		model.setTestDate(source.getTestDate() );
		model.setTestExempted(source.getTestExempted() );
		model.setTestExemptedReason(source.getTestExemptedReason() );
		model.setTestNoOfAttemp(source.getTestNoOfAttemp() );
		model.setTestResult(source.getTestResult() );
		model.setTestMarks(source.getTestMarks() );
		return model;
	}

	@Override
	public LicensePermitDetailsEntity convertToEntity(LicensePermitDetailsModel source) {
		
		if(ObjectsUtil.isNull(source)){
			return null;
		}
		LicensePermitDetailsEntity entity = new LicensePermitDetailsEntity();
			
		entity.setLicenseNumber(source.getLicenseNumber() );
		entity.setLicenseType(source.getLicenseType() );
		entity.setVehicleClassCode(source.getVehicleClassCode() );
		entity.setTestDate(source.getTestDate() );
		entity.setTestExempted(source.getTestExempted() );
		entity.setTestExemptedReason(source.getTestExemptedReason() );
		entity.setTestNoOfAttemp(source.getTestNoOfAttemp() );
		entity.setTestResult(source.getTestResult() );
		entity.setTestMarks(source.getTestMarks() );
		return entity;
	}

	@Override
	public Collection<LicensePermitDetailsModel> convertToModelList(Collection<LicensePermitDetailsEntity> source) {
		if (ObjectsUtil.isNull(source)) {
            return null;
        }
        return source.stream().map(s -> convertToModel(s)).collect(Collectors.toList());
	}

	@Override
	public Collection<LicensePermitDetailsEntity> convertToEntityList(Collection<LicensePermitDetailsModel> source) {
		if (ObjectsUtil.isNull(source)) {
            return null;
        }
        return source.stream().map(s -> convertToEntity(s)).collect(Collectors.toList());
	}

}
