package org.rta.citizen.common.converters;

import java.util.Collection;

import org.rta.citizen.common.entity.AddressOutsideAPEntity;
import org.rta.citizen.common.enums.AddressType;
import org.rta.citizen.common.model.AddressModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Component;

/**
 *	@Author sohan.maurya created on Dec 16, 2016.
 */
@Component
public class AddressOutsideAPConverter implements BaseConverter<AddressOutsideAPEntity, AddressModel> {

    @Override
    public AddressModel convertToModel(AddressOutsideAPEntity source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        AddressModel model = new AddressModel();
        model.setDoorNo(source.getDoorNo());
        model.setStreet(source.getStreetName());
        model.setCity(source.getTownName());
        model.setStateName(source.getStateName());
        model.setDistrictName(source.getDistrictName());
        model.setCountryName(source.getCountryName());
        model.setType(AddressType.getAddressType(source.getAddressType()));

        return model;
    }

    @Override
    public AddressOutsideAPEntity convertToEntity(AddressModel source) {
        if (ObjectsUtil.isNull(source)) {
            return null;
        }
        AddressOutsideAPEntity entity = new AddressOutsideAPEntity();
        entity.setStatus(Boolean.TRUE);
        entity.setDoorNo(source.getDoorNo());
        entity.setStreetName(source.getStreet());
        entity.setTownName(source.getCity());
        entity.setMandalName(source.getMandalName());
        entity.setDistrictName(source.getDistrictName());
        entity.setStateName(source.getStateName());
        entity.setCountryName(source.getCountryName());
        entity.setPincode(String.valueOf(source.getPostOffice()));
        return entity;
    }

    @Override
    public Collection<AddressModel> convertToModelList(Collection<AddressOutsideAPEntity> source) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<AddressOutsideAPEntity> convertToEntityList(Collection<AddressModel> source) {
        // TODO Auto-generated method stub
        return null;
    }

}
