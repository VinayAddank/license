package org.rta.citizen.slotbooking.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.rta.citizen.common.dao.ApplicationDAO;
import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.DayModel;
import org.rta.citizen.common.model.RTAOfficeModel;
import org.rta.citizen.common.model.RTAOfficeNameComparator;
import org.rta.citizen.common.model.ReceiptModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.rta.citizen.common.model.UserSessionModel;
import org.rta.citizen.common.service.CalendarService;
import org.rta.citizen.common.service.registration.RegistrationService;
import org.rta.citizen.common.utils.DateUtil;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.common.utils.ServiceUtil;
import org.rta.citizen.common.utils.StringsUtil;
import org.rta.citizen.slotbooking.aspects.Notifiable;
import org.rta.citizen.slotbooking.converters.SlotConverter;
import org.rta.citizen.slotbooking.dao.SlotDAO;
import org.rta.citizen.slotbooking.entity.SlotApplicationsEntity;
import org.rta.citizen.slotbooking.entity.SlotEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.enums.SlotStatus;
import org.rta.citizen.slotbooking.exception.SlotNotOccupiedException;
import org.rta.citizen.slotbooking.exception.SlotUnavailableException;
import org.rta.citizen.slotbooking.model.RTAOfficeScheduleModel;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.RTAOfficeSchedulingService;
import org.rta.citizen.slotbooking.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javassist.NotFoundException;

@Service
public class SlotServiceImpl implements SlotService {

	private static final Logger log = Logger.getLogger(SlotServiceImpl.class);
    
    private static Map<ServiceType,List<SlotServiceType>> applicableSlotTypesMap = new HashMap<>();

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private SlotDAO slotDAO;

    @Autowired
    private SlotConverter slotConverter;

    @Autowired
    private RTAOfficeSchedulingService rtaOfficeSchedulingService;

    @Value(value = "${slot.hold.time.seconds}")
    private Long SLOT_HOLD_TIME_IN_SECONDS;
    
    @Value(value = "${slot.occupy.attempt.count}")
    private Integer SLOT_OCCOPY_ATTEMPT_COUNT;
    
    @PostConstruct
    public void populateApplicableSlotTypes() {
        
        // registration slot type mappings
        applicableSlotTypesMap.put(ServiceType.REGISTRATION_RENEWAL, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        applicableSlotTypesMap.put(ServiceType.VEHICLE_ATLERATION, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        applicableSlotTypesMap.put(ServiceType.DIFFERENTIAL_TAX, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        
        // License slot type mappings
        applicableSlotTypesMap.put(ServiceType.LL_FRESH, Arrays.asList(SlotServiceType.DRIVING_LICENCE_MACHINE_TEST));
        applicableSlotTypesMap.put(ServiceType.LL_RETEST, Arrays.asList(SlotServiceType.DRIVING_LICENCE_MACHINE_TEST));
        applicableSlotTypesMap.put(ServiceType.DL_FRESH, Arrays.asList(SlotServiceType.HMV_TEST,SlotServiceType.THREE_FOUR_WHEELER_TEST
                , SlotServiceType.TWO_WHEELER_TEST));
        applicableSlotTypesMap.put(ServiceType.DL_ENDORSMENT, Arrays.asList(SlotServiceType.HMV_TEST,SlotServiceType.THREE_FOUR_WHEELER_TEST
                , SlotServiceType.TWO_WHEELER_TEST));
        applicableSlotTypesMap.put(ServiceType.DL_EXPIRED, Arrays.asList(SlotServiceType.HMV_TEST,SlotServiceType.THREE_FOUR_WHEELER_TEST
                , SlotServiceType.TWO_WHEELER_TEST));
        applicableSlotTypesMap.put(ServiceType.DL_RETEST, Arrays.asList(SlotServiceType.HMV_TEST,SlotServiceType.THREE_FOUR_WHEELER_TEST
                , SlotServiceType.TWO_WHEELER_TEST));
        
        // Fitness slot type mappings
        applicableSlotTypesMap.put(ServiceType.FC_FRESH, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        applicableSlotTypesMap.put(ServiceType.FC_OTHER_STATION, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        applicableSlotTypesMap.put(ServiceType.FC_RE_INSPECTION_SB, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        applicableSlotTypesMap.put(ServiceType.FC_RENEWAL, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
        applicableSlotTypesMap.put(ServiceType.FC_REVOCATION_CFX, Arrays.asList(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY));
     
    }
    
    @Override
    public List<SlotServiceType> getApplicableSlots(ServiceType serviceType) {
        return applicableSlotTypesMap.get(serviceType);
    }

    @Override
    @Transactional
    public List<DayModel> getAvailableSlots(UserSessionModel session, Long startTime, Long endTime, SlotServiceType slotType, ServiceType serviceType) {
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (applicationEntity == null) {
            log.error("no application found");
            return null;
        }
        List<DayModel> slots = new ArrayList<DayModel>();
        Set<Long> days = new LinkedHashSet<>();
        while (startTime.longValue() <= endTime.longValue() - DateUtil.ONE_DAY_SECONDS) {
            days.add(startTime);
            startTime = startTime + DateUtil.ONE_DAY_SECONDS;
        } ;
        Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(applicationEntity.getRtaOfficeCode());
        List<Long> unavailableTimeSlots;
        for (Long day : days) {
            unavailableTimeSlots = slotDAO.getBookedSlots(applicationEntity.getRtaOfficeCode(), day);
            Set<Long> availableSlots = new LinkedHashSet<>();
            // remove all unavailable slot of the day
            for (Long slotTime : allSlots) {
                if (!unavailableTimeSlots.contains(slotTime.longValue())) {
                    availableSlots.add(slotTime.longValue());
                }
            }
            // if time is available on this day then considered it as available day or slot is available
            if (availableSlots.size() > 0 && !calendarService.isHoliday(day)) {
                slots.add(new DayModel(day));
            }
        }
        return slots;
    }
    
    @Override
    @Transactional
    public List<DayModel> getAvailableSlots(UserSessionModel session, Long startTime, Long endTime, List<SlotServiceType> slotType, ServiceType serviceType, String rtaOfficeCode) {
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (applicationEntity == null) {
            log.error("no application found");
            return null;
        }
        Set<Long> days = new LinkedHashSet<>();
        while (startTime.longValue() <= endTime.longValue() - DateUtil.ONE_DAY_SECONDS) {
            days.add(startTime);
            startTime = startTime + DateUtil.ONE_DAY_SECONDS;
        } ;
        Map<Long, List<SlotServiceType>> map = new TreeMap<>();
        for (Long day : days) {
            map.put(day, new ArrayList<>());
        }
        for (SlotServiceType sst : slotType) {
            // prefer 
            String officeCode = (!StringsUtil.isNullOrEmpty(rtaOfficeCode) && serviceType == ServiceType.FC_OTHER_STATION) ? rtaOfficeCode : applicationEntity.getRtaOfficeCode();
            Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(officeCode, sst, ServiceUtil.getSlotCategory(serviceType));
            List<Long> unavailableTimeSlots;
            for (Long day : days) {
                unavailableTimeSlots = slotDAO.getBookedSlots(applicationEntity.getRtaOfficeCode(), day, sst, ServiceUtil.getSlotCategory(serviceType));
                Set<Long> availableSlots = new LinkedHashSet<>();
                // remove all unavailable slot of the day
                for (Long slotTime : allSlots) {
                    if (!unavailableTimeSlots.contains(slotTime.longValue())) {
                        availableSlots.add(slotTime.longValue());
                    }
                }
                // if time is available on this day then considered it as available day or slot is available
                if (availableSlots.size() > 0 && !calendarService.isHoliday(day)) {
//                    slots.add(new DayModel(day));
                    map.get(day).add(sst);
                }
            }
        }
        List<DayModel> finalDays = new ArrayList<>();
        for (Map.Entry<Long, List<SlotServiceType>> entry : map.entrySet()) {
            if (entry.getValue().size() > 0) {
                finalDays.add(new DayModel(entry.getKey()));
            }
        }
        return finalDays;
    }

    @Override
    @Transactional
    public List<DayModel> getBookedSlots(UserSessionModel session, Long startTime, Long endTime, SlotServiceType slotType, ServiceType serviceType) {
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (applicationEntity == null) {
            log.error("no application found");
            return null;
        }
        List<DayModel> slots = new ArrayList<DayModel>();
        Set<Long> days = new LinkedHashSet<>();
        while (startTime.longValue() <= endTime.longValue() - DateUtil.ONE_DAY_SECONDS) {
            days.add(startTime);
            startTime = startTime + DateUtil.ONE_DAY_SECONDS;
        } ;
        Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(applicationEntity.getRtaOfficeCode());
        List<Long> unavailableTimeSlots;
        for (Long day : days) {
            unavailableTimeSlots = slotDAO.getBookedSlots(applicationEntity.getRtaOfficeCode(), day);
            Set<Long> availableSlots = new LinkedHashSet<>();
            // remove all unavailable slot of the day
            for (Long slotTime : allSlots) {
                if (!unavailableTimeSlots.contains(slotTime.longValue())) {
                    availableSlots.add(slotTime.longValue());
                }
            }
            // if no time is available on this day then considered it as booked day or no slot is available
            if (availableSlots.size() == Integer.valueOf(0)) {
                slots.add(new DayModel(day));
            }
        }
        return slots;
    }
    
    @Override
    @Transactional
    public List<DayModel> getBookedSlots(UserSessionModel session, Long startTime, Long endTime, List<SlotServiceType> slotType, ServiceType serviceType, String rtaOfficeCode) {
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (applicationEntity == null) {
            log.error("no application found");
            return null;
        }
        Set<Long> days = new LinkedHashSet<>();
        while (startTime.longValue() <= endTime.longValue() - DateUtil.ONE_DAY_SECONDS) {
            days.add(startTime);
            startTime = startTime + DateUtil.ONE_DAY_SECONDS;
        } ;
        Map<Long, List<SlotServiceType>> map = new TreeMap<>();
        for (Long day : days) {
            map.put(day, new ArrayList<>());
        }
        for (SlotServiceType sst : slotType) {
            String officeCode = (!StringsUtil.isNullOrEmpty(rtaOfficeCode) && serviceType == ServiceType.FC_OTHER_STATION) ? rtaOfficeCode : applicationEntity.getRtaOfficeCode();
            Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(officeCode, sst, ServiceUtil.getSlotCategory(serviceType));
            List<Long> unavailableTimeSlots;
            for (Long day : days) {
                unavailableTimeSlots = slotDAO.getBookedSlots(officeCode, day, sst, ServiceUtil.getSlotCategory(serviceType));
                Set<Long> availableSlots = new LinkedHashSet<>();
                // remove all unavailable slot of the day
                for (Long slotTime : allSlots) {
                    if (!unavailableTimeSlots.contains(slotTime.longValue())) {
                        availableSlots.add(slotTime.longValue());
                    }
                }
                // if no time is available on this day then considered it as booked day or no slot is available
                if (availableSlots.size() == Integer.valueOf(0)) {
//                    slots.add(new DayModel(day));
                    map.get(day).add(sst);
                }
            }
        }
        List<DayModel> finalDays = new ArrayList<>();
        for (Map.Entry<Long, List<SlotServiceType>> entry : map.entrySet()) {
            if (entry.getValue().size() > 0) {
                finalDays.add(new DayModel(entry.getKey()));
            }
        }
        return finalDays;
    }

    @Override
    @Transactional
    public SlotModel occupy(UserSessionModel session, SlotModel dayModel, ServiceType serviceType) throws SlotUnavailableException {
        if (calendarService.isHoliday(dayModel.getScheduledDate())) {
            log.info("can't occupy slot on holiday, timestamp : " + dayModel.getScheduledDate());
            throw new SlotUnavailableException("Slot is unavailable on the holiday");
        }
        SlotModel slotModel = null;
        String rtaOfficeCode = null;
        RTAOfficeModel rtaOfficeModel = null;
        ApplicationEntity applicationEntity =
                applicationDAO.getApplicationFromSession(session.getSessionId());
        rtaOfficeCode = applicationEntity.getRtaOfficeCode();
        RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponseModel = null;
        try {
            rtaOfficeResponseModel = registrationService.getRtaDetails(rtaOfficeCode);
        } catch (UnauthorizedException e1) {
            log.error("slot occupy : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        } catch (HttpClientErrorException e) {
            log.error("slot occupy : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        if (rtaOfficeResponseModel.getHttpStatus() == HttpStatus.OK) {
            rtaOfficeModel = rtaOfficeResponseModel.getResponseBody();
        }
        List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO.getBookedSlotByApplicationIdAndIteration(applicationEntity.getApplicationId(), applicationEntity.getIteration());
        if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
            log.info("slot already booked for application number : " + applicationEntity.getApplicationNumber() + " and iteration : " + applicationEntity.getIteration());
            throw new SlotUnavailableException("slot already booked");
        }
        SlotEntity availableSlot;
        Long currentTime = DateUtil.toCurrentUTCTimeStamp();
        RTAOfficeScheduleModel rtaOfficeScheduleModel = rtaOfficeSchedulingService.getSchedule(rtaOfficeCode);
        synchronized (this) {
            try {
                removeExpiredSlots(rtaOfficeScheduleModel, dayModel.getScheduledDate(), currentTime);
                availableSlot = getAvailableSlot(session, rtaOfficeScheduleModel, dayModel.getScheduledDate(), currentTime, applicationEntity.getApplicationId(), applicationEntity.getIteration());
                SlotApplicationsEntity slotEntity = slotDAO.getActiveSlotByApplicationId(applicationEntity.getApplicationId(), currentTime, availableSlot.getSlotId(), applicationEntity.getIteration());
                if (ObjectsUtil.isNull(slotEntity)) {
                    Integer applicationCount = availableSlot.getApplicationCount();
                    Integer finalCount = applicationCount + 1;
                    availableSlot.setApplicationCount(finalCount);
                    if (finalCount >= rtaOfficeScheduleModel.getNumberOfSimultaneousSlots()) {
                        availableSlot.setIsCompleted(Boolean.TRUE);
                    }
                    slotEntity = new SlotApplicationsEntity();
                    slotEntity.setCreatedBy(session.getAadharNumber());
                    slotEntity.setCreatedOn(currentTime);
                }
                slotEntity.setIteration(applicationEntity.getIteration());
                slotEntity.setApplication(applicationEntity);
                slotEntity.setExpiryTime(DateUtil.toCurrentUTCTimeStamp() + SLOT_HOLD_TIME_IN_SECONDS);
                slotEntity.setModifiedBy(session.getAadharNumber());
                slotEntity.setModifiedOn(currentTime);
                slotEntity.setServiceCode(serviceType.getCode());
                slotEntity.setSlot(availableSlot);
                /*if (serviceType == ServiceType.TRANSPORT_NEW_REGISTRATION) {
                        slotApplicationEntity.setSlotServiceType(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY);
                    } else {
                        slotApplicationEntity.setSlotServiceType(SlotServiceType.DRIVING_LICENCE);
                    }*/
                slotEntity.setSlotStatus(SlotStatus.OCCUPIED);
                slotDAO.saveOrUpdateSlotApplications(slotEntity);
                slotModel = slotConverter.convertToModel(availableSlot);
                slotModel.getRtaOfficeModel().setName(rtaOfficeModel.getName());
                slotModel.getRtaOfficeModel().setRtaOfficeId(rtaOfficeModel.getRtaOfficeId());
                slotModel.getRtaOfficeModel().setAddress(rtaOfficeModel.getAddress());
                slotModel.setStartTime(availableSlot.getStartTime());
                return slotModel;
            } catch (Exception e) {
                log.info("last available time get attempt failed.. retrying..");
            }
        }
        if (ObjectsUtil.isNull(slotModel)) {
            log.info("unable to get slots for date : " + dayModel.getScheduledDate());
            throw new SlotUnavailableException("Slot is unavailable on the selected date");
        }
        return null;
    }
    
    @Override
    @Transactional
    public List<SlotModel> occupy(UserSessionModel session, List<SlotModel> dayModel, ServiceType serviceType, boolean sameDay) throws SlotUnavailableException, NotFoundException {
        Long day;
        Integer numberOfSlots;
        validateSlotServiceType(dayModel, serviceType);
        validateCurrentDaySlot(dayModel);
        if (sameDay) {
            day = validateSameDay(dayModel);
            numberOfSlots = dayModel.size();
        } else {
            // always considering only one date is there 
            if (dayModel.size() > 1) {
                log.info("can't book multiple slots");
                throw new IllegalArgumentException("can't book multiple slots");
            }
            day = dayModel.get(0).getScheduledDate();
            numberOfSlots = 1;
        }
        validateForHoliday(day);
        List<SlotModel> slotModel = new ArrayList<>();
        String rtaOfficeCode = null;
        RTAOfficeModel rtaOfficeModel = null;
        ApplicationEntity applicationEntity =
                applicationDAO.getApplicationFromSession(session.getSessionId());
        if (applicationEntity == null) {
            log.error("application not found for session Id : " + session.getSessionId());
            throw new NotFoundException("application not found ");
        }
        rtaOfficeCode = applicationEntity.getRtaOfficeCode();
        RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponseModel = null;
        try {
            rtaOfficeResponseModel = registrationService.getRtaDetails(rtaOfficeCode);
        } catch (UnauthorizedException e1) {
            log.error("slot occupy : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        } catch (HttpClientErrorException e) {
            log.error("slot occupy : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        if (rtaOfficeResponseModel.getHttpStatus() == HttpStatus.OK) {
            rtaOfficeModel = rtaOfficeResponseModel.getResponseBody();
        }
        List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO.getBookedSlotByApplicationIdAndIteration(applicationEntity.getApplicationId(), applicationEntity.getIteration());
        if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
            log.info("slot already booked for application number : " + applicationEntity.getApplicationNumber() + " and iteration : " + applicationEntity.getIteration());
            throw new SlotUnavailableException("slot already booked");
        }
        List<SlotEntity> availableSlots = null;
        List<Long> availableSlotsTime = new ArrayList<>();
        Long currentTime = DateUtil.toCurrentUTCTimeStamp();
        
        RTAOfficeScheduleModel rtaOfficeScheduleModel = null;//null;rtaOfficeSchedulingService.getSchedule(rtaOfficeCode, SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY, ServiceCategory.FC_CATEGORY);
        
        // TODO
        synchronized (this) {
            try {
                removeExpiredSlots(rtaOfficeScheduleModel, day, currentTime);
                availableSlots = getAvailableSlots(session, rtaOfficeScheduleModel, day, currentTime, applicationEntity.getApplicationId(), applicationEntity.getIteration(), numberOfSlots, dayModel.stream().map(d->d.getType()).collect(Collectors.toList()));
                if (sameDay) {
                    Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(rtaOfficeScheduleModel.getRtaOfficeCode());
                    // sort in natural order
                    List<Long> allSlotsList = new ArrayList<>(allSlots);
                    Collections.sort(allSlotsList);
                    allSlotsList.retainAll(availableSlotsTime);
                    Long duration = rtaOfficeScheduleModel.getDuration();
                    for (int i=0; i < allSlotsList.size() - 1; i++) {
                        if (allSlotsList.get(i) + duration == allSlotsList.get(i++)) {
                            continue;
                        }
                        throw new SlotUnavailableException("slots can't be on same day");
                    }
                }
                int i=0;
                for (SlotEntity se : availableSlots) {
                    SlotApplicationsEntity slotEntity = slotDAO.getActiveSlotByApplicationId(applicationEntity.getApplicationId(), currentTime, se.getSlotId(), applicationEntity.getIteration());
                    if (ObjectsUtil.isNull(slotEntity)) {
                        Integer applicationCount = se.getApplicationCount();
                        Integer finalCount = applicationCount + 1;
                        se.setApplicationCount(finalCount);
                        if (finalCount >= rtaOfficeScheduleModel.getNumberOfSimultaneousSlots()) {
                            se.setIsCompleted(Boolean.TRUE);
                        }
                        slotEntity = new SlotApplicationsEntity();
                        slotEntity.setCreatedBy(session.getAadharNumber());
                        slotEntity.setCreatedOn(currentTime);
                    }
                    slotEntity.setIteration(applicationEntity.getIteration());
                    slotEntity.setApplication(applicationEntity);
                    slotEntity.setExpiryTime(DateUtil.toCurrentUTCTimeStamp() + SLOT_HOLD_TIME_IN_SECONDS);
                    slotEntity.setModifiedBy(session.getAadharNumber());
                    slotEntity.setModifiedOn(currentTime);
                    slotEntity.setServiceCode(serviceType.getCode());
                    slotEntity.setSlot(se);
                    slotEntity.setSlotServiceType(dayModel.get(i).getType());
                    slotEntity.setSlotStatus(SlotStatus.OCCUPIED);
                    slotEntity.setApprovalStatus(Status.PENDING.getValue());
                    slotDAO.saveOrUpdateSlotApplications(slotEntity);
                    SlotModel slotEntityModel = slotConverter.convertToModel(se);
                    slotEntityModel.getRtaOfficeModel().setName(rtaOfficeModel.getName());
                    slotEntityModel.getRtaOfficeModel().setRtaOfficeId(rtaOfficeModel.getRtaOfficeId());
                    slotEntityModel.getRtaOfficeModel().setAddress(rtaOfficeModel.getAddress());
                    slotEntityModel.setStartTime(se.getStartTime());
                    slotEntityModel.setType(slotEntity.getSlotServiceType());
                    slotModel.add(slotEntityModel);
                    i++;
                }
                return slotModel;
            } catch (SlotUnavailableException e) {
                log.error("an error occurred while booking slot for application : " + applicationEntity.getApplicationNumber());
                throw e;
            }
        }
    }
    
    @Override
    @Transactional
    public List<SlotModel> occupyUpdated(UserSessionModel session, List<SlotModel> dayModel, ServiceType serviceType, boolean sameDay, String officeCode) throws SlotUnavailableException, NotFoundException {
        Long day;
        Integer numberOfSlots;
        validateSlotServiceType(dayModel, serviceType);
        validateCurrentDaySlot(dayModel);
        if (sameDay) {
            day = validateSameDay(dayModel);
            numberOfSlots = dayModel.size();
        } else {
            // always considering only one date is there 
            if (dayModel.size() > 1) {
                log.info("can't book multiple slots");
                throw new IllegalArgumentException("can't book multiple slots");
            }
            day = dayModel.get(0).getScheduledDate();
            numberOfSlots = 1;
        }
        validateForHoliday(day);
        List<SlotModel> slotModel = new ArrayList<>();
        String rtaOfficeCode = null;
        RTAOfficeModel rtaOfficeModel = null;
        ApplicationEntity applicationEntity =
                applicationDAO.getApplicationFromSession(session.getSessionId());
        if (applicationEntity == null) {
            log.error("application not found for session Id : " + session.getSessionId());
            throw new NotFoundException("application not found ");
        }
        if (!StringsUtil.isNullOrEmpty(officeCode) && serviceType == ServiceType.FC_OTHER_STATION) {
            rtaOfficeCode = officeCode;
        } else {
            rtaOfficeCode = applicationEntity.getRtaOfficeCode();
        }
        RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponseModel = null;
        try {
            rtaOfficeResponseModel = registrationService.getRtaDetails(rtaOfficeCode);
        } catch (UnauthorizedException e1) {
            log.error("slot occupy : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        } catch (HttpClientErrorException e) {
            log.error("slot occupy : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        if (rtaOfficeResponseModel.getHttpStatus() == HttpStatus.OK) {
            rtaOfficeModel = rtaOfficeResponseModel.getResponseBody();
        }
        List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO.getBookedSlotByApplicationIdAndIteration(applicationEntity.getApplicationId(), applicationEntity.getIteration());
        if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
            log.info("slot already booked for application number : " + applicationEntity.getApplicationNumber() + " and iteration : " + applicationEntity.getIteration());
            throw new SlotUnavailableException("slot already booked");
        }
        List<SlotEntity> availableSlots = null;
        List<Long> availableSlotsTime = new ArrayList<>();
        Long currentTime = DateUtil.toCurrentUTCTimeStamp();
        Long duration = null;
        List<RTAOfficeScheduleModel> rtaOfficeScheduleModelList = new ArrayList<>();
        for (SlotModel slotsRequestModel : dayModel) {
            RTAOfficeScheduleModel officeModel = rtaOfficeSchedulingService.getSchedule(rtaOfficeCode, slotsRequestModel.getType(), ServiceUtil.getSlotCategory(serviceType));
            if (!ObjectsUtil.isNull(officeModel)) {
                duration = officeModel.getDuration();
                rtaOfficeScheduleModelList.add(officeModel);
            }
        }
        synchronized (this) {
            try {
                // expire all currently occupied slots if sameday is selected 
                if (sameDay) {
                     if (!expireAlreadyOccupiedSlots(dayModel, day, applicationEntity, currentTime, rtaOfficeScheduleModelList)) {
                         removeExpiredSlots(rtaOfficeScheduleModelList, day, currentTime);
                     }
                } else {
                    removeExpiredSlots(rtaOfficeScheduleModelList, day, currentTime);
                }
                availableSlots = getAvailableSlots(session, rtaOfficeScheduleModelList, day, currentTime, applicationEntity.getApplicationId(), applicationEntity.getIteration(), numberOfSlots, dayModel.stream().map(d->d.getType()).collect(Collectors.toList()), serviceType, rtaOfficeCode, sameDay);
                for (SlotEntity slot : availableSlots) {
                    if (sameDay) {
                        Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(rtaOfficeCode, slot.getSlotServiceType(), SlotCategory.getServiceTypeCat(slot.getServiceCategory()));
                        // sort in natural order
                        List<Long> allSlotsList = new ArrayList<>(allSlots);
                        Collections.sort(allSlotsList);
                        allSlotsList.retainAll(availableSlotsTime);
                        for (int i=0; i < allSlotsList.size() - 1; i++) {
                            if (allSlotsList.get(i) + duration == allSlotsList.get(i++)) {
                                continue;
                            }
                            throw new SlotUnavailableException("slots can't be on same day");
                        }
                    }
                    SlotApplicationsEntity slotEntity = slotDAO.getActiveSlotByApplicationId(applicationEntity.getApplicationId(), currentTime, slot.getSlotId(), applicationEntity.getIteration());
                    if (ObjectsUtil.isNull(slotEntity)) {
                        Integer applicationCount = slot.getApplicationCount();
                        Integer finalCount = applicationCount + 1;
                        slot.setApplicationCount(finalCount);
                        RTAOfficeScheduleModel officeModel = rtaOfficeSchedulingService.getSchedule(rtaOfficeCode, slot.getSlotServiceType(), ServiceUtil.getSlotCategory(serviceType));
                        if (finalCount >= officeModel.getNumberOfSimultaneousSlots()) {
                            slot.setIsCompleted(Boolean.TRUE);
                        }
                        slotEntity = new SlotApplicationsEntity();
                        slotEntity.setCreatedBy(session.getAadharNumber());
                        slotEntity.setCreatedOn(currentTime);
                    }
                    slotEntity.setIteration(applicationEntity.getIteration());
                    slotEntity.setApplication(applicationEntity);
                    slotEntity.setExpiryTime(DateUtil.toCurrentUTCTimeStamp() + SLOT_HOLD_TIME_IN_SECONDS);
                    slotEntity.setModifiedBy(session.getAadharNumber());
                    slotEntity.setModifiedOn(currentTime);
                    slotEntity.setServiceCode(serviceType.getCode());
                    slotEntity.setSlot(slot);
                    slotEntity.setSlotServiceType(slot.getSlotServiceType());
                    slotEntity.setSlotStatus(SlotStatus.OCCUPIED);
                    slotEntity.setApprovalStatus(Status.PENDING.getValue());
                    slotDAO.saveOrUpdateSlotApplications(slotEntity);
                    SlotModel slotEntityModel = slotConverter.convertToModel(slot);
                    slotEntityModel.getRtaOfficeModel().setName(rtaOfficeModel.getName());
                    slotEntityModel.getRtaOfficeModel().setRtaOfficeId(rtaOfficeModel.getRtaOfficeId());
                    slotEntityModel.getRtaOfficeModel().setAddress(rtaOfficeModel.getAddress());
                    slotEntityModel.setStartTime(slot.getStartTime());
                    slotEntityModel.setType(slotEntity.getSlotServiceType());
                    slotModel.add(slotEntityModel);
                }
                return slotModel;
            } catch (SlotUnavailableException e) {
                log.error("an error occurred while booking slot for application : " + applicationEntity.getApplicationNumber());
                throw e;
            }
        }
    }

    private boolean expireAlreadyOccupiedSlots(List<SlotModel> dayModel, Long day, ApplicationEntity applicationEntity,
            Long currentTime, List<RTAOfficeScheduleModel> rtaOfficeScheduleModelList) {
        boolean expired = false;
        for (RTAOfficeScheduleModel rtaOfficeScheduleCode : rtaOfficeScheduleModelList) {
            List<SlotApplicationsEntity> slotEntity = slotDAO.getActiveSlotByApplicationIdDateAndType(applicationEntity.getApplicationId(), currentTime, day, applicationEntity.getIteration(), dayModel.stream().map(d->d.getType()).collect(Collectors.toList()));
            if (!ObjectsUtil.isNullOrEmpty(slotEntity)) {
                for (SlotApplicationsEntity se : slotEntity) {
                    if (se.getSlot().getSlotServiceType() == rtaOfficeScheduleCode.getSlotServiceType()) {
                        se.setSlotStatus(SlotStatus.EXPIRED);
                        SlotEntity slot = se.getSlot();
                        Integer applicationCount = slot.getApplicationCount();
                        Integer finalCount = applicationCount - 1;
                        slot.setApplicationCount(finalCount);
                        if (finalCount < rtaOfficeScheduleCode.getNumberOfSimultaneousSlots()) {
                            slot.setIsCompleted(Boolean.FALSE);
                        }
                        slotDAO.saveOrUpdateSlotApplications(se);
                        expired = true;
                    }
                }
            }
        }
        return expired;
    }

    public void validateCurrentDaySlot(List<SlotModel> dayModel) throws SlotUnavailableException {
        Long currentTimeMilliSeconds = DateUtil.toCurrentUTCTimeStampMilliSeconds();
        for (SlotModel slot : dayModel) {
            Long scheduledDate = slot.getScheduledDate();
            Long parsedDate = DateUtil.dateSeconds(scheduledDate * 1000).longValue();
            if (DateUtil.dateSeconds(currentTimeMilliSeconds).longValue() >= parsedDate) {
                log.info("slot can be booked from next day only");
                throw new SlotUnavailableException("slot can be booked from next day only");
            }
            // set only date of current day in GMT time zone
            slot.setScheduledDate(parsedDate);
        }
    }

    public void validateSlotServiceType(List<SlotModel> dayModel, ServiceType serviceType) {
        List<SlotServiceType> applicableSlotServiceType = applicableSlotTypesMap.get(serviceType);
        if (ObjectsUtil.isNull(applicableSlotServiceType)) {
            log.debug("invalid service type : " + serviceType);
            throw new IllegalArgumentException("invalid service type");
        }
        for (SlotModel sm : dayModel) {
            if (!applicableSlotServiceType.contains(sm.getType())) {
                log.debug("invalid slot type : " + sm.getType() +" for service type :  " + serviceType);
                throw new IllegalArgumentException("invalid slot type");
            }
        }
    }

    private void validateForHoliday(Long day) throws SlotUnavailableException {
        if (calendarService.isHoliday(day)) {
            log.info("can't occupy slot on holiday, timestamp : " + day);
            throw new SlotUnavailableException("Slot is unavailable on the holiday");
        }
    }

    private Long validateSameDay(List<SlotModel> dayModel) {
        Long day = null;
        Map<Long, Object> map = new HashMap<>();
        for (SlotModel slot : dayModel) {
            day = slot.getScheduledDate();
            map.put(day, ObjectsUtil.PRESENT);
        }
        if (map.size() != 1) {
            log.info("dates are different : " + map);
            throw new IllegalArgumentException("dates are different");
        }
        return day;
    }

    @Override
    @Transactional
    public void removeExpiredSlots(RTAOfficeScheduleModel rtaOfficeScheduleCode, Long date, Long currentTime) {
        List<SlotApplicationsEntity> rtaOfficeDaySlots = slotDAO.getExpiredSlots(rtaOfficeScheduleCode.getRtaOfficeCode(), currentTime, date);
        for (SlotApplicationsEntity slotEntity : rtaOfficeDaySlots) {
            slotEntity.setSlotStatus(SlotStatus.EXPIRED);
            SlotEntity slot = slotEntity.getSlot();
            Integer applicationCount = slot.getApplicationCount();
            Integer finalCount = applicationCount - 1;
            slot.setApplicationCount(finalCount);
            if (finalCount < rtaOfficeScheduleCode.getNumberOfSimultaneousSlots()) {
                slot.setIsCompleted(Boolean.FALSE);
            }
            slotDAO.saveOrUpdateSlotApplications(slotEntity);
        }
    }
    
    @Override
    @Transactional
    public void removeExpiredSlots(List<RTAOfficeScheduleModel> rtaOfficeScheduleCodeList, Long date, Long currentTime) {
        for (RTAOfficeScheduleModel rtaOfficeScheduleCode : rtaOfficeScheduleCodeList) {
            List<SlotApplicationsEntity> rtaOfficeDaySlots = slotDAO.getExpiredSlots(rtaOfficeScheduleCode.getRtaOfficeCode(), currentTime, date);
            for (SlotApplicationsEntity slotEntity : rtaOfficeDaySlots) {
                if (slotEntity.getSlot().getSlotServiceType() == rtaOfficeScheduleCode.getSlotServiceType()) {
                    slotEntity.setSlotStatus(SlotStatus.EXPIRED);
                    SlotEntity slot = slotEntity.getSlot();
                    Integer applicationCount = slot.getApplicationCount();
                    Integer finalCount = applicationCount - 1;
                    slot.setApplicationCount(finalCount);
                    if (finalCount < rtaOfficeScheduleCode.getNumberOfSimultaneousSlots()) {
                        slot.setIsCompleted(Boolean.FALSE);
                    }
                    slotDAO.saveOrUpdateSlotApplications(slotEntity);
                }
            }
        }
    }

    @Override
    @Transactional
    public ResponseModel<ReceiptModel> confirm(UserSessionModel session, Long slotId) throws SlotNotOccupiedException, UnauthorizedException {
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (ObjectsUtil.isNull(applicationEntity)) {
            log.error("no application found for session : " + session.getSessionId());
            throw new UnauthorizedException();
        }
        List<SlotApplicationsEntity> slotApplicationEntity = slotDAO.getSlotApplication(applicationEntity.getApplicationId(), slotId, DateUtil.toCurrentUTCTimeStamp());
        if (ObjectsUtil.isNullOrEmpty(slotApplicationEntity)) {
            log.error("no application found for session : " + session.getSessionId());
            throw new SlotNotOccupiedException("slot not occupied exception");
        }
        for(SlotApplicationsEntity sae : slotApplicationEntity) {
            sae.setSlotStatus(SlotStatus.BOOKED);
            slotDAO.saveOrUpdateSlotApplications(sae);
        }
        if (session.getServiceType() == ServiceType.DIFFERENTIAL_TAX) {
            try {
                updateApplications(session, applicationEntity, slotApplicationEntity);
            } catch (Exception e) {
                log.info("marking " + session.getKeyType() + " " + session.getUniqueKey() + " as not updated ");
                // TODO : need to log later
            }
        }
        return new ResponseModel<>(ResponseModel.SUCCESS);
    }
    
    @Override
    @Notifiable
    @Transactional
    public ResponseModel<ReceiptModel> confirm(UserSessionModel session, List<SlotModel> slotIds) throws SlotNotOccupiedException, UnauthorizedException, NotFoundException, SlotUnavailableException {
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (ObjectsUtil.isNull(applicationEntity)) {
            log.error("no application found for session : " + session.getSessionId());
            throw new UnauthorizedException();
        }
        List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO.getBookedSlotByApplicationIdAndIteration(applicationEntity.getApplicationId(), applicationEntity.getIteration());
        if (!ObjectsUtil.isNullOrEmpty(slotApplicationEntityList)) {
            log.info("slot already booked for application number : " + applicationEntity.getApplicationNumber() + " and iteration : " + applicationEntity.getIteration());
            throw new SlotUnavailableException("slot already booked");
        }
        List<SlotApplicationsEntity> slotApplicationEntity = slotDAO.getSlotApplication(applicationEntity.getApplicationId(), 
                slotIds.stream().map(ids->ids.getSlotId()).collect(Collectors.toList()), DateUtil.toCurrentUTCTimeStamp());
        if (ObjectsUtil.isNullOrEmpty(slotApplicationEntity)) {
            log.error("no application found for session : " + session.getSessionId());
            throw new SlotNotOccupiedException("slot not occupied exception");
        }
        if (slotIds.size() != slotApplicationEntity.size()) {
            log.error("must confirm all the slots at once : " + session.getSessionId());
            throw new SlotNotOccupiedException("must confirm all the slots at once");
        }
        String rtaOfficeCode = null;
        ReceiptModel receipt = new ReceiptModel();
        receipt.setServiceType(session.getServiceType().getCode());
        receipt.setAppNumber(applicationEntity.getApplicationNumber());
        List<SlotModel> slotModelList = new ArrayList<>();
        for(SlotApplicationsEntity sae : slotApplicationEntity) {
            sae.setSlotStatus(SlotStatus.BOOKED);
            SlotEntity slotEntity = sae.getSlot();
            SlotModel slotEntityModel = slotConverter.convertToModel(slotEntity);
            slotEntityModel.setScheduledTime(slotEntity.getScheduledTime());
            slotEntityModel.setScheduledDate(slotEntity.getScheduledDate());
            slotEntityModel.setType(slotEntity.getSlotServiceType());
            slotModelList.add(slotEntityModel);
            if (StringsUtil.isNullOrEmpty(rtaOfficeCode) && ServiceType.getServiceType(applicationEntity.getServiceCode()) == ServiceType.FC_OTHER_STATION) {
                rtaOfficeCode = slotEntity.getRtaOfficeCode();
                applicationEntity.setRtaOfficeCode(rtaOfficeCode);
                applicationDAO.saveOrUpdate(applicationEntity);
            }
            slotDAO.saveOrUpdateSlotApplications(sae);
        }
        receipt.setSlotModel(slotModelList);
        if (rtaOfficeCode == null) {
            rtaOfficeCode = applicationEntity.getRtaOfficeCode();
        }
        // getting RTA office
        RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponseModel = null;
        try {
            rtaOfficeResponseModel = registrationService.getRtaDetails(rtaOfficeCode);
        } catch (UnauthorizedException e1) {
            log.error("slot occupy : error in getting rta office");
            throw new NotFoundException("rta office not found");
        } catch (HttpClientErrorException e) {
            log.error("slot occupy : error in getting rta office");
            throw new NotFoundException("rta office not found");
        }
        if (rtaOfficeResponseModel.getHttpStatus() == HttpStatus.OK) {
            receipt.setRtaOfficeModel(rtaOfficeResponseModel.getResponseBody());
        }
        if (session.getServiceType() == ServiceType.DIFFERENTIAL_TAX) {
            try {
                updateApplications(session, applicationEntity, slotApplicationEntity);
            } catch (Exception e) {
                log.error("marking " + session.getKeyType() + " " + session.getUniqueKey() + " as not updated ");
                // TODO : need to log later
            }
        }
        return new ResponseModel<>(ResponseModel.SUCCESS, receipt);
    }
    
    private void updateApplications(UserSessionModel session, ApplicationEntity applicationEntity,
            List<SlotApplicationsEntity> saea) throws UnauthorizedException {
        List<SlotModel> slotsList = new ArrayList<>();
        CitizenApplicationModel citizenApplicationModel = new CitizenApplicationModel();
        citizenApplicationModel.setAppId(applicationEntity.getApplicationId());
        citizenApplicationModel.setApplicationNumber(applicationEntity.getApplicationNumber());
        citizenApplicationModel.setKeyType(session.getKeyType());
        citizenApplicationModel.setUniqueKey(session.getUniqueKey());
        citizenApplicationModel.setServiceType(session.getServiceType());
        for (SlotApplicationsEntity sae : saea) {
            SlotModel slotModel = new SlotModel();
            slotModel.setApplicationId(applicationEntity.getApplicationId());
    //            slotModel.setDuration(duration);
            slotModel.setEndTime(sae.getSlot().getEndTime());
    //            slotModel.setRtaOfficeModel(rtaOfficeModel);
            slotModel.setType(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY);
            slotModel.setScheduledDate(sae.getSlot().getScheduledDate());
            slotModel.setScheduledTime(sae.getSlot().getScheduledTime());
            slotModel.setSlotId(sae.getSlot().getSlotId());
            slotModel.setSlotStatus(sae.getSlotStatus());
            slotModel.setStartTime(sae.getSlot().getStartTime());
            slotModel.setType(sae.getSlotServiceType());
            slotsList.add(slotModel);
        }
        citizenApplicationModel.setSlot(slotsList);
        try {
            @SuppressWarnings("unused")
            RegistrationServiceResponseModel<SaveUpdateResponse> response = registrationService.saveSlots(citizenApplicationModel);
        } catch (HttpClientErrorException e) {
            log.error("slotbooking : error when saving slot info in registration module. status code : " + e.getStatusCode());
            throw e;
        } catch (HttpServerErrorException e) {
            log.error("slotbooking : error when saving slot info in registration module. status code : " + e.getStatusCode());
            throw e;
        }
    }
    
    private SlotEntity getAvailableSlot(UserSessionModel session, RTAOfficeScheduleModel rtaOfficeScheduleModel, Long date, Long currentTime, Long applicationId, Integer iteration) throws SlotUnavailableException {
        Long finalAvailableTime;
        Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(rtaOfficeScheduleModel.getRtaOfficeCode());
        if (ObjectsUtil.isNullOrEmpty(allSlots)) {
            log.info("no slot found for rta office code : " + rtaOfficeScheduleModel.getRtaOfficeCode());
            throw new SlotUnavailableException("slot unavailable");
        }
        SlotEntity slot;
        SlotApplicationsEntity slotEntity = slotDAO.getActiveSlotByApplicationIdAndDate(applicationId, currentTime, date, iteration);
        if (ObjectsUtil.isNull(slotEntity)) {
            List<Long> bookedSlots = slotDAO.getBookedSlots(rtaOfficeScheduleModel.getRtaOfficeCode(), date);
            List<Long> availabledTimeSlots = new ArrayList<>(allSlots);
            if (!ObjectsUtil.isNullOrEmpty(bookedSlots)) {
                availabledTimeSlots.removeAll(bookedSlots);
                if (ObjectsUtil.isNullOrEmpty(allSlots)) {
                    log.info("no slot found for rta office code : " + rtaOfficeScheduleModel.getRtaOfficeCode());
                    throw new SlotUnavailableException("slot unavailable");
                }
            }
            Collections.sort(availabledTimeSlots);
            finalAvailableTime = availabledTimeSlots.get(0);
            slot =  slotDAO.getSlot(rtaOfficeScheduleModel.getRtaOfficeCode(), date, finalAvailableTime);
            if (ObjectsUtil.isNull(slot)) {
                slot = new SlotEntity();
                slot.setApplicationCount(0);
                slot.setCreatedBy(session.getAadharNumber());
                slot.setCreatedOn(currentTime);
                slot.setDuration(rtaOfficeScheduleModel.getDuration());
                slot.setEndTime(finalAvailableTime + rtaOfficeScheduleModel.getDuration());
                slot.setRtaOfficeCode(rtaOfficeScheduleModel.getRtaOfficeCode());
                slot.setScheduledDate(date);
                slot.setScheduledTime(date + finalAvailableTime);
                slot.setServiceCode(session.getServiceType().getCode());
                slot.setStartTime(finalAvailableTime);
                slot.setIsCompleted(Boolean.FALSE);
            }
            if (slot.getIsCompleted()) {
                log.info("slot already booked");
                throw new SlotUnavailableException("slots unavailable");
            }
            slot.setModifiedBy(session.getAadharNumber());
            slot.setModifiedOn(currentTime);
            slotDAO.saveOrUpdate(slot);
        } else {
            slot = slotEntity.getSlot();
        }
        return slot;
    }
    
    private List<SlotEntity> getAvailableSlots(UserSessionModel session, RTAOfficeScheduleModel rtaOfficeScheduleModel, Long date, Long currentTime, Long applicationId, Integer iteration, Integer numerOfSlots, List<SlotServiceType> slotServiceTypeList) throws SlotUnavailableException {
        Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(rtaOfficeScheduleModel.getRtaOfficeCode());
        if (ObjectsUtil.isNullOrEmpty(allSlots)) {
            log.info("no slot found for rta office code : " + rtaOfficeScheduleModel.getRtaOfficeCode());
            throw new SlotUnavailableException("slot unavailable");
        }
        List<SlotEntity> slots = new ArrayList<>();
        List<SlotApplicationsEntity> slotEntity = slotDAO.getActiveSlotByApplicationIdDateAndType(applicationId, currentTime, date, iteration, slotServiceTypeList);
        if (ObjectsUtil.isNullOrEmpty(slotEntity)) {
            List<Long> bookedSlots = slotDAO.getBookedSlots(rtaOfficeScheduleModel.getRtaOfficeCode(), date);
            List<Long> availabledTimeSlots = new ArrayList<>(allSlots);
            if (!ObjectsUtil.isNullOrEmpty(bookedSlots)) {
                availabledTimeSlots.removeAll(bookedSlots);
                if (ObjectsUtil.isNullOrEmpty(allSlots)) {
                    log.info("no slot found for rta office code : " + rtaOfficeScheduleModel.getRtaOfficeCode());
                    throw new SlotUnavailableException("slot unavailable");
                }
            }
            Collections.sort(availabledTimeSlots);
            Long duration = rtaOfficeScheduleModel.getDuration();
            availabledTimeSlots = ObjectsUtil.findAP(availabledTimeSlots, numerOfSlots, duration.intValue());
            if (ObjectsUtil.isNullOrEmpty(availabledTimeSlots)) {
                log.info("consecutive slots not found");
                throw new SlotUnavailableException("slots not available");
            }
            for (Long slotTime : availabledTimeSlots) {
                SlotEntity slot = slotDAO.getSlot(rtaOfficeScheduleModel.getRtaOfficeCode(), date, slotTime);
                if (ObjectsUtil.isNull(slot)) {
                    slot = new SlotEntity();
                    slot.setApplicationCount(0);
                    slot.setCreatedBy(session.getAadharNumber());
                    slot.setCreatedOn(currentTime);
                    slot.setDuration(rtaOfficeScheduleModel.getDuration());
                    slot.setEndTime(slotTime + rtaOfficeScheduleModel.getDuration());
                    slot.setRtaOfficeCode(rtaOfficeScheduleModel.getRtaOfficeCode());
                    slot.setScheduledDate(date);
                    slot.setScheduledTime(date + slotTime);
                    slot.setServiceCode(session.getServiceType().getCode());
                    slot.setStartTime(slotTime);
                    slot.setIsCompleted(Boolean.FALSE);
                }
                if (slot.getIsCompleted()) {
                    log.info("slot already booked");
                    throw new SlotUnavailableException("slots unavailable");
                }
                slot.setModifiedBy(session.getAadharNumber());
                slot.setModifiedOn(currentTime);
                slotDAO.saveOrUpdate(slot);
                slots.add(slot);
            }
        } else {
            for (SlotApplicationsEntity sap : slotEntity) {
                slots.add(sap.getSlot());
            }
        }
        return slots;
    }
    
    private List<SlotEntity> getAvailableSlots(UserSessionModel session, List<RTAOfficeScheduleModel> rtaOfficeScheduleModelList, Long date, Long currentTime, Long applicationId, Integer iteration, Integer numerOfSlots, List<SlotServiceType> slotServiceTypeList, ServiceType serviceType, String rtaOfficeCode, boolean sameDay) throws SlotUnavailableException {
        List<SlotEntity> slots = new ArrayList<>();
        SlotCategory serviceCategory = ServiceUtil.getSlotCategory(serviceType);
        List<SlotApplicationsEntity> slotEntity = slotDAO.getActiveSlotByApplicationIdDateAndType(applicationId, currentTime, date, iteration, slotServiceTypeList);
        if (sameDay || ObjectsUtil.isNullOrEmpty(slotEntity)) {
            List<SlotModel> finalAvailableSlots = new ArrayList<>();
//            List<Long> finalAvailableTimeSlots = new ArrayList<>();
            Long duration = null;
            for (RTAOfficeScheduleModel rtaOfficeScheduleModel : rtaOfficeScheduleModelList) {
                duration = rtaOfficeScheduleModel.getDuration(); // assuming duration will be same
                Set<Long> allSlots = rtaOfficeSchedulingService.getTimeSlots(rtaOfficeScheduleModel.getRtaOfficeCode(), rtaOfficeScheduleModel.getSlotServiceType(), serviceCategory);
                if (ObjectsUtil.isNullOrEmpty(allSlots)) {
                    log.info("no slot found for rta office code : " + rtaOfficeScheduleModel.getRtaOfficeCode());
                    throw new SlotUnavailableException("slot unavailable");
                }
                List<Long> bookedSlots = slotDAO.getBookedSlots(rtaOfficeScheduleModel.getRtaOfficeCode(), date, rtaOfficeScheduleModel.getSlotServiceType(), serviceCategory);
                List<Long> availabledTimeSlots = new ArrayList<>(allSlots);
                if (!ObjectsUtil.isNullOrEmpty(bookedSlots)) {
                    availabledTimeSlots.removeAll(bookedSlots);
                    if (ObjectsUtil.isNullOrEmpty(availabledTimeSlots)) {
                        log.info("no slot found for rta office code : " + rtaOfficeScheduleModel.getRtaOfficeCode());
                        throw new SlotUnavailableException("slot unavailable");
                    }
                }
                
                Collections.sort(availabledTimeSlots);
//                finalAvailableTimeSlots.addAll(availabledTimeSlots);
                for (Long timeSlot : availabledTimeSlots) {
                    finalAvailableSlots.add(new SlotModel(timeSlot, rtaOfficeScheduleModel.getSlotServiceType(), serviceCategory.getCode()));
                }
            }
            
            finalAvailableSlots = findNewSlots(finalAvailableSlots, numerOfSlots, duration.intValue(), slotServiceTypeList);
            if (ObjectsUtil.isNullOrEmpty(finalAvailableSlots)) {
                log.info("consecutive slots not found");
                throw new SlotUnavailableException("slots not available");
            }
            for (SlotModel slotTime : finalAvailableSlots) {
                Long startTime = slotTime.getStartTime();
                SlotEntity slot = slotDAO.getSlot(rtaOfficeCode, date, startTime, slotTime.getType(), serviceCategory);
                if (ObjectsUtil.isNull(slot)) {
                    slot = new SlotEntity();
                    slot.setApplicationCount(0);
                    slot.setCreatedBy(session.getAadharNumber());
                    slot.setCreatedOn(currentTime);
                    slot.setDuration(duration);
                    slot.setEndTime(startTime + duration);
                    slot.setRtaOfficeCode(rtaOfficeCode);
                    slot.setScheduledDate(date);
                    slot.setScheduledTime(date + startTime);
                    slot.setServiceCode(session.getServiceType().getCode());
                    slot.setStartTime(startTime);
                    slot.setIsCompleted(Boolean.FALSE);
                    slot.setSlotServiceType(slotTime.getType());
                    slot.setServiceCategory(slotTime.getServiceCategory());
                }
                if (slot.getIsCompleted()) {
                    log.info("slot already booked");
                    throw new SlotUnavailableException("slots unavailable");
                }
                slot.setModifiedBy(session.getAadharNumber());
                slot.setModifiedOn(currentTime);
                slotDAO.saveOrUpdate(slot);
                slots.add(slot);
            }
        } else {
            for (SlotApplicationsEntity sap : slotEntity) {
                slots.add(sap.getSlot());
            }
        }
        return slots;
    }
    

    @Override
    @Transactional
    public List<SlotModel> getReceipt(UserSessionModel session) throws SlotUnavailableException {
        List<SlotModel> slots = new ArrayList<>();
        ApplicationEntity applicationEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        RTAOfficeModel rtaOfficeModel = null;
        RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponseModel = null;
        try {
            rtaOfficeResponseModel = registrationService.getRtaDetails(applicationEntity.getRtaOfficeCode());
        } catch (UnauthorizedException e1) {
            log.error("slot receipt : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        } catch (HttpClientErrorException e) {
            log.error("slot receipt : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        if (rtaOfficeResponseModel.getHttpStatus() == HttpStatus.OK) {
            rtaOfficeModel = rtaOfficeResponseModel.getResponseBody();
        }
        List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO.getBookedSlot(applicationEntity.getApplicationId(), applicationEntity.getIteration());
        if (ObjectsUtil.isNull(slotApplicationEntityList)) {
            log.error("slot receipt : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        for (SlotApplicationsEntity slot : slotApplicationEntityList) {
            SlotEntity slotEntity = slot.getSlot();
            SlotModel slotModel = slotConverter.convertToModel(slotEntity);
            slotModel.setApplicationId(slot.getApplication().getApplicationId());
            slotModel.getRtaOfficeModel().setName(rtaOfficeModel.getName());
            slotModel.getRtaOfficeModel().setRtaOfficeId(rtaOfficeModel.getRtaOfficeId());
            slotModel.getRtaOfficeModel().setAddress(rtaOfficeModel.getAddress());
            slotModel.setType(slot.getSlotServiceType());
            slots.add(slotModel);
        }
        return slots;
    }    
    @Override
    @Transactional
    public List<SlotModel> getSlotBookingDetails(String applicationNumner) throws SlotUnavailableException {
        List<SlotModel> slots = new ArrayList<>();
        ApplicationEntity applicationEntity = applicationDAO.getApplication(applicationNumner);
        RTAOfficeModel rtaOfficeModel = null;
        RegistrationServiceResponseModel<RTAOfficeModel> rtaOfficeResponseModel = null;
        try {
            rtaOfficeResponseModel = registrationService.getRtaDetails(applicationEntity.getRtaOfficeCode());
        } catch (UnauthorizedException e1) {
            log.error("slot receipt : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        } catch (HttpClientErrorException e) {
            log.error("slot receipt : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        if (rtaOfficeResponseModel.getHttpStatus() == HttpStatus.OK) {
            rtaOfficeModel = rtaOfficeResponseModel.getResponseBody();
        }
        List<SlotApplicationsEntity> slotApplicationEntityList = slotDAO.getBookedSlot(applicationEntity.getApplicationId(), applicationEntity.getIteration());
        if (ObjectsUtil.isNull(slotApplicationEntityList)) {
            log.error("slot receipt : error in getting rta office");
            throw new SlotUnavailableException("slot not available");
        }
        for (SlotApplicationsEntity slot : slotApplicationEntityList) {
            SlotEntity slotEntity = slot.getSlot();
            SlotModel slotModel = slotConverter.convertToModel(slotEntity);
            slotModel.setApplicationId(slot.getApplication().getApplicationId());
            slotModel.getRtaOfficeModel().setName(rtaOfficeModel.getName());
            slotModel.getRtaOfficeModel().setRtaOfficeId(rtaOfficeModel.getRtaOfficeId());
            slotModel.getRtaOfficeModel().setAddress(rtaOfficeModel.getAddress());
            slotModel.setType(slot.getSlotServiceType());
            slots.add(slotModel);
        }
        return slots;
    }
    
    private static List<SlotModel> findContinuousSlots(List<SlotModel> list, int subsetSize, int commonDifference) {
        Stack<SlotModel> stack = new Stack<>();
        for (int i=0; i<list.size();i++) {
            SlotModel currentSlot = list.get(i);
            if (!stack.isEmpty()) {
                SlotModel lastSlot = stack.peek();
                if (lastSlot.getStartTime().longValue() + commonDifference == currentSlot.getStartTime().longValue() 
                        && lastSlot.getServiceCategory().equals(currentSlot.getServiceCategory()) 
                        && lastSlot.getType() != currentSlot.getType()) {
                    stack.push(currentSlot);
                } else {
                    if (stack.size() != subsetSize) {
                        stack.clear();
                    }
                    stack.push(currentSlot);
                }
            } else {
                stack.push(currentSlot);
            }
            if (stack.size() >= subsetSize) {
                break;
            }
        }
        if (stack.size() < subsetSize) {
            stack.clear();
        }
        return new ArrayList<>(stack);
    }
    
    private static List<SlotModel> findNewSlots(List<SlotModel> list, int subsetSize, int commonDifference, List<SlotServiceType> slotServiceTypeList) throws SlotUnavailableException {
        Collections.sort(list);
        Map<Long,List<SlotModel>> map1 = new TreeMap<>();
        for (SlotModel sm : list) {
            List<SlotModel> mm = map1.get(sm.getStartTime());
            if (mm == null) {
                mm = new ArrayList<>();
                mm.add(sm);
                map1.put(sm.getStartTime(), mm);
            } else {
                mm.add(sm);
            }
        }
        Map<Long,List<SlotModel>> map = map1;//sortByValue(map1);
        List<Long> tempTimes = new ArrayList<>();
        List<SlotModel> tempSlots = new ArrayList<>();
        
        Set<Entry<Long, List<SlotModel>>> s = map.entrySet();
        for (Entry<Long, List<SlotModel>> entry : s) {
            List<SlotModel> smmm = entry.getValue();
            if (tempSlots.isEmpty()) {
                tempSlots.add(smmm.get(0));
            } else {
                for (SlotModel avSlot : smmm) {
                    for (SlotModel sm : tempSlots) {
                        if (!tempTimes.contains(avSlot.getStartTime())) {
                            if (sm.getType() != avSlot.getType() && sm.getStartTime() + commonDifference == avSlot.getStartTime()) {
                                tempSlots.add(avSlot);
                                tempTimes.add(avSlot.getStartTime());
                                break;
                            }
                        }
                    }
                }
            }
            if (tempSlots.size() >= subsetSize) {
                return tempSlots;
            }
        }
        if (tempSlots.size() != subsetSize) {
            tempSlots.clear();
        }
        return tempSlots;
    }
    
    private static Map<Long, List<SlotModel>> sortByValue(Map<Long, List<SlotModel>> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Long, List<SlotModel>>> list =
                new LinkedList<Map.Entry<Long, List<SlotModel>>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Long, List<SlotModel>>>() {
            public int compare(Map.Entry<Long, List<SlotModel>> o1,
                               Map.Entry<Long, List<SlotModel>> o2) {
                Integer size1= o1.getValue().size();
                Integer size2= o2.getValue().size();
                return (size1).compareTo(size2);
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Long, List<SlotModel>> sortedMap = new LinkedHashMap<Long, List<SlotModel>>();
        for (Map.Entry<Long, List<SlotModel>> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }
    
    
    private static List<SlotModel> findSlots(Stack<SlotModel> stack, List<SlotModel> list, List<SlotServiceType> existingType, List<SlotModel> remainingSlots, int subsetSize, int commonDifference) {
        if (stack.size() >= subsetSize) {
            return new ArrayList<>(stack);
        }
        for (SlotModel sm : list) {
            if (stack.isEmpty()) {
                stack.push(sm);
            }
            if (stack.peek().getStartTime().longValue() + commonDifference == sm.getStartTime().longValue()) {
                stack.push(sm);
            } else {
                stack.pop();
            }
        }
        existingType = stack.stream().map(e->e.getType()).collect(Collectors.toList());
        for (SlotModel mm : list) {
            if (!existingType.contains(mm.getType())) {
                remainingSlots.add(mm);
            }
        }
        return findSlots(stack, list, existingType, remainingSlots, subsetSize, commonDifference);
    }
    
    private static void permutation(String prefix, String str, List<String> per) {
        int n = str.length();
        if (n == 0) {
            per.add(prefix);
        } else {
            for (int i = 0; i < n; i++) {
                permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n), per);
            }
        }
    }
    
    public static void permutation(String str) {
        //permutation("", str); 
    }
    
    public static void main(String[] args) {
        List<SlotModel> s1 = new ArrayList<>();
        s1.add(new SlotModel(16200L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
        s1.add(new SlotModel(19800L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
//        s1.add(new SlotModel(23400L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
//        s1.add(new SlotModel(27000L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
        
        s1.add(new SlotModel(16200L, SlotServiceType.THREE_FOUR_WHEELER_TEST, "DL"));
        s1.add(new SlotModel(19800L, SlotServiceType.THREE_FOUR_WHEELER_TEST, "DL"));
        
        s1.add(new SlotModel(16200L, SlotServiceType.HMV_TEST, "DL"));
        s1.add(new SlotModel(19800L, SlotServiceType.HMV_TEST, "DL"));
        
        
        List<SlotModel> s2 = new ArrayList<>();
//        s2.add(new SlotModel(16200L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
        s2.add(new SlotModel(19800L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
//        s1.add(new SlotModel(23400L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
//        s1.add(new SlotModel(27000L, SlotServiceType.TWO_WHEELER_TEST, "DL"));
        
        s2.add(new SlotModel(16200L, SlotServiceType.THREE_FOUR_WHEELER_TEST, "DL"));
        s1.add(new SlotModel(19800L, SlotServiceType.THREE_FOUR_WHEELER_TEST, "DL"));
        
        s2.add(new SlotModel(16200L, SlotServiceType.HMV_TEST, "DL"));
        s2.add(new SlotModel(19800L, SlotServiceType.HMV_TEST, "DL"));
        
        System.out.println(s1.retainAll(s2));
        System.out.println(s1);
//        s1.add(new SlotModel(23400L, SlotServiceType.THREE_FOUR_WHEELER_TEST, "DL"));
//        s1.add(new SlotModel(27000L, SlotServiceType.THREE_FOUR_WHEELER_TEST, "DL"));
        /*List<SlotModel> newList = findContinuousSlotss(s1, 3, 3600);
        System.out.println(newList.size());
        for (SlotModel sss : newList) {
            System.out.println(sss);
        }*/
//        List<String> a =  new ArrayList<>();
//        permutation("", "ABC", a);
//        System.out.println(a);
        
        try {
            List<SlotModel> sm = findNewSlots(s1, 3, 3600, Arrays.asList(SlotServiceType.HMV_TEST, SlotServiceType.THREE_FOUR_WHEELER_TEST, SlotServiceType.TWO_WHEELER_TEST));
        } catch (SlotUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Override
    @Transactional
    public List<RTAOfficeModel> getRTAOfficeByState(UserSessionModel session, String stateCode) {
        ApplicationEntity appEntity = applicationDAO.getApplicationFromSession(session.getSessionId());
        if (!ObjectsUtil.isNull(appEntity)) {
            String appOffice = appEntity.getRtaOfficeCode();
            List<RTAOfficeModel> otherOffices = null;
            RegistrationServiceResponseModel<List<RTAOfficeModel>> allOffices = null;
            try {
                allOffices = registrationService.getRTAOfficeByState(stateCode);
                List<RTAOfficeModel> offices = allOffices.getResponseBody();
                if (!ObjectsUtil.isNull(offices)) {
                    otherOffices = new ArrayList<>();
                    for (RTAOfficeModel o : offices) {
                        if (!o.getCode().equalsIgnoreCase(appOffice)) {
                            otherOffices.add(o);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("error in getting RTA office list for sessionId : " + session.getSessionId(), e);
            }
            if (!ObjectsUtil.isNullOrEmpty(otherOffices)) {
                Collections.sort(otherOffices, new RTAOfficeNameComparator());
            }
            return otherOffices;
        }
        return null;
    }
    
    @Override
    public KeyType getKeyType() {
        return KeyType.TR;
    }

    @Override
    public String getUniqueKey(UserSessionModel session) {
        return session.getUniqueKey();
    }

    @Override
    public SlotServiceType getSlotServiceType() {
        return SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY;
    }
    
    public List<SlotModel> getSlots(Long timestamp) {
        return null;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DIFFERENTIAL_TAX;
    }

}
