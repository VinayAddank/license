package org.rta.citizen.slotbooking.service;

import java.util.List;

import org.rta.citizen.BaseService;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.DayModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.exception.SlotNotOccupiedException;
import org.rta.citizen.slotbooking.exception.SlotUnavailableException;
import org.rta.citizen.slotbooking.model.RTAOfficeScheduleModel;
import org.rta.citizen.slotbooking.model.SlotModel;

import javassist.NotFoundException;

public interface SlotService extends BaseService {

    public List<DayModel> getBookedSlots(UserSessionModel session, Long startTime, Long endTime, SlotServiceType slotType, ServiceType serviceType);

    public SlotModel occupy(UserSessionModel session, SlotModel dayModel, ServiceType serviceType) throws SlotUnavailableException;
    
    public ResponseModel<ReceiptModel> confirm(UserSessionModel session, Long slotId) throws SlotNotOccupiedException, UnauthorizedException;
    
    public SlotServiceType getSlotServiceType();
    
    public ServiceType getServiceType();

    public List<DayModel> getAvailableSlots(UserSessionModel session, Long startTime, Long endTime, SlotServiceType typeOfSlot, ServiceType newParam);

    void removeExpiredSlots(RTAOfficeScheduleModel rtaOfficeScheduleCode, Long date, Long currentTime);

    public List<SlotModel> getReceipt(UserSessionModel session) throws SlotUnavailableException;

    public List<SlotModel> occupy(UserSessionModel session, List<SlotModel> dayModel, ServiceType serviceType, boolean isContinuous)
                throws SlotUnavailableException, NotFoundException;

    ResponseModel<ReceiptModel> confirm(UserSessionModel session, List<SlotModel> slotIds)
            throws SlotNotOccupiedException, UnauthorizedException, NotFoundException, SlotUnavailableException;
    public List<SlotModel> getSlotBookingDetails(String applicationNumner) throws SlotUnavailableException;

    List<SlotServiceType> getApplicableSlots(ServiceType serviceType);

    List<SlotModel> occupyUpdated(UserSessionModel session, List<SlotModel> dayModel, ServiceType serviceType,
            boolean sameDay, String officeCode) throws SlotUnavailableException, NotFoundException;

    void removeExpiredSlots(List<RTAOfficeScheduleModel> rtaOfficeScheduleCodeList, Long date, Long currentTime);

    List<DayModel> getBookedSlots(UserSessionModel session, Long startTime, Long endTime,
            List<SlotServiceType> slotType, ServiceType serviceType, String rtaOfficeCode);

    List<DayModel> getAvailableSlots(UserSessionModel session, Long startTime, Long endTime,
            List<SlotServiceType> slotType, ServiceType serviceType, String rtaOfficeCode);

    List<RTAOfficeModel> getRTAOfficeByState(UserSessionModel session, String stateCode);
    
}
