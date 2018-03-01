package org.rta.citizen.common.converters;

import org.rta.citizen.common.entity.HolidayEntity;
import org.rta.citizen.common.model.DayModel;
import org.springframework.stereotype.Component;

@Component
public class HolidayConverter {

    public DayModel convertToHolidayEntity(HolidayEntity entity) {
        DayModel holidayModel = new DayModel();
        holidayModel.setTimestamp(entity.getDate());
        return holidayModel;
    }
    
}
