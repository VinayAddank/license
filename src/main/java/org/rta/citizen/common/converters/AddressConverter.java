package org.rta.citizen.common.converters;

import java.util.Collection;

import org.rta.citizen.common.entity.AddressEntity;
import org.rta.citizen.common.enums.AddressType;
import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

/**
 *	@Author sohan.maurya created on Dec 16, 2016.
 */
@Component
public class AddressConverter implements BaseConverter<AddressEntity, AddressModel> {

    @Override
    public AddressModel convertToModel(AddressEntity source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        AddressModel model = new AddressModel();
        model.setDoorNo(source.getDoorNo());
        model.setStreet(source.getStreetName());
        model.setCity(source.getTownName());
        model.setStateCode(source.getStateCode());
        model.setDistrictCode(source.getDistrictCode());
        model.setCountryCode(source.getCountryCode());
        model.setType(AddressType.getAddressType(source.getAddressType()));
        model.setIsSameAadhar(source.getIsSameAadhar());
        model.setPostOffice(Long.valueOf(source.getPincode()));
        return model;
    }

    @Override
    public AddressEntity convertToEntity(AddressModel source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        AddressEntity entity = new AddressEntity();
        entity.setStatus(Boolean.TRUE);
        entity.setDoorNo(source.getDoorNo());
        entity.setStreetName(source.getStreet());
        entity.setTownName(source.getCity());
        entity.setMandalCode(source.getMandalCode());
        entity.setDistrictCode(source.getDistrictCode());
        entity.setStateCode(source.getStateCode());
        entity.setCountryCode(source.getCountryCode());
        entity.setPincode(String.valueOf(source.getPostOffice()));
        entity.setIsSameAadhar(source.getIsSameAadhar());
        return entity;
    }

    @Override
    public Collection<AddressModel> convertToModelList(Collection<AddressEntity> source) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<AddressEntity> convertToEntityList(Collection<AddressModel> source) {
        // TODO Auto-generated method stub
        return null;
    }

}
