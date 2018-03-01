package org.rta.citizen.slotbooking.converters;

import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.slotbooking.entity.SlotEntity;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.springframework.stereotype.Component;

@Component
public class SlotConverter {

    public SlotModel convertToModel(SlotEntity entity) {
        if (ObjectsUtil.isNull(entity)) {
            return null;
        }

        SlotModel model = new SlotModel();
        model.setDuration(entity.getDuration());
        model.setEndTime(entity.getEndTime());
        RTAOfficeModel rtaOffice = new RTAOfficeModel();
        rtaOffice.setCode(entity.getRtaOfficeCode());
        model.setRtaOfficeModel(rtaOffice);
        model.setScheduledDate(entity.getScheduledDate());
        model.setScheduledTime(entity.getScheduledTime());
        model.setSlotId(entity.getSlotId());
//        model.setSlotStatus(SlotStatus.getSlotStatus(entity.getSlotStatus()));
        model.setStartTime(entity.getStartTime());
//        model.setType(entity.get);
        return model;
    }

}
