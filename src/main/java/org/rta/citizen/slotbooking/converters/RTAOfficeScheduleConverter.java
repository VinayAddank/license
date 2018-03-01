package org.rta.citizen.slotbooking.converters;

import org.rta.citizen.slotbooking.entity.RTAOfficeScheduleEntity;
import org.rta.citizen.slotbooking.model.RTAOfficeScheduleModel;
import org.springframework.stereotype.Component;

@Component
public class RTAOfficeScheduleConverter {

    public RTAOfficeScheduleModel convertToModel(RTAOfficeScheduleEntity entity) {
        if (entity == null) {
            return null;
        }
        RTAOfficeScheduleModel model = new RTAOfficeScheduleModel();
//        model.setDuration(entity.getR);
        model.setEndTime(entity.getEndTime());
        model.setIsEnabled(entity.getIsEnabled());
        model.setRtaOfficeCode(entity.getRtaOfficeCode());
        model.setRtaOfficeScheduleId(entity.getRtaOfficeScheduleId());
        model.setStartTime(entity.getStartTime());
        model.setNumberOfSimultaneousSlots(entity.getNumberOfSimultaneousSlots());
        return model;
    }

}
