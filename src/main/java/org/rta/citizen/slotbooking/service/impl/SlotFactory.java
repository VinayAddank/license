package org.rta.citizen.slotbooking.service.impl;

import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.slotbooking.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;

//@Component
public class SlotFactory {

    @Autowired
    private SlotServiceImpl slotServiceImpl;
    
    public SlotService getSlotService(ServiceType slotServiceType) {
        if (ServiceType.DIFFERENTIAL_TAX == slotServiceType) {
            return slotServiceImpl;
        }
        return slotServiceImpl;
    }
    
}
