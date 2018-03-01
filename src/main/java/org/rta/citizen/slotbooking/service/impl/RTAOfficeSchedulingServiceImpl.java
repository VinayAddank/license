package org.rta.citizen.slotbooking.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.rta.citizen.common.dao.RTAOfficeTestConfigDAO;
import org.rta.citizen.common.enums.SlotCategory;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.slotbooking.converters.RTAOfficeScheduleConverter;
import org.rta.citizen.slotbooking.dao.RTAOfficeSchedulingDAO;
import org.rta.citizen.slotbooking.entity.RTAOfficeScheduleEntity;
import org.rta.citizen.slotbooking.entity.RTAOfficeTestConfigEntity;
import org.rta.citizen.slotbooking.enums.SlotServiceType;
import org.rta.citizen.slotbooking.model.RTAOfficeScheduleModel;
import org.rta.citizen.slotbooking.model.SlotModel;
import org.rta.citizen.slotbooking.service.RTAOfficeSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RTAOfficeSchedulingServiceImpl implements RTAOfficeSchedulingService {

    @Autowired
    private RTAOfficeSchedulingDAO rtaOfficeSchedulingDAO;

    @Autowired
    private RTAOfficeScheduleConverter rtaOfficeScheduleConverter;
    
    @Autowired
    private RTAOfficeTestConfigDAO rtaOfficeTestConfigDAO;

    @Override
    @Transactional
    public RTAOfficeScheduleModel getSchedule(String code) {
        return rtaOfficeScheduleConverter.convertToModel(rtaOfficeSchedulingDAO.getSchedule(code));
    }
    
    @Override
    @Transactional
    public RTAOfficeScheduleModel getSchedule(String code, SlotServiceType slotServiceType, SlotCategory serviceCategory) {
        RTAOfficeTestConfigEntity rtaOfficeTestConfigEntity = rtaOfficeSchedulingDAO.getConfig(code, slotServiceType, serviceCategory);
        RTAOfficeScheduleEntity rtaOfficeScheduleEntity = rtaOfficeTestConfigEntity.getRtaOfficeSchedule();
        RTAOfficeScheduleModel rtaOfficeScheduleModel = rtaOfficeScheduleConverter.convertToModel(rtaOfficeScheduleEntity);
        rtaOfficeScheduleModel.setDuration(rtaOfficeTestConfigEntity.getDuration());
        rtaOfficeScheduleModel.setNumberOfSimultaneousSlots(rtaOfficeTestConfigEntity.getSimulApplicationCount());
        rtaOfficeScheduleModel.setRtaOfficeScheduleId(rtaOfficeScheduleEntity.getRtaOfficeScheduleId());
        rtaOfficeScheduleModel.setSlotServiceType(rtaOfficeTestConfigEntity.getSlotServiceType());
        return rtaOfficeScheduleModel;
    }

    @Override
    public Set<Long> getTimeSlots(String code) {
        RTAOfficeScheduleModel rtaOfficeModel = getSchedule(code);
        Set<Long> timeSlots;
        if (!ObjectsUtil.isNull(rtaOfficeModel)) {
            timeSlots = new LinkedHashSet<>();
            Long startTime = rtaOfficeModel.getStartTime();
            Long duration = rtaOfficeModel.getDuration();
            while (startTime <= rtaOfficeModel.getEndTime() - duration) {
                timeSlots.add(startTime);
                startTime = startTime + duration;
            } ;
            return timeSlots;
        }
        return null;
    }
    
    @Override
    public Set<Long> getTimeSlots(String code, SlotServiceType slotServiceType, SlotCategory serviceCategory) {
        RTAOfficeScheduleModel rtaOfficeModel = getSchedule(code, slotServiceType, serviceCategory);
        Set<Long> timeSlots;
        if (!ObjectsUtil.isNull(rtaOfficeModel)) {
            timeSlots = new LinkedHashSet<>();
            Long startTime = rtaOfficeModel.getStartTime();
            Long duration = rtaOfficeModel.getDuration();
            while (startTime <= rtaOfficeModel.getEndTime() - duration) {
                timeSlots.add(startTime);
                startTime = startTime + duration;
            } ;
            return timeSlots;
        }
        return null;
    }
    
    @Override
    public List<SlotModel> getTimeSlots(Long startTime, Long endTime, Long duration) {
        List<SlotModel> slots = new ArrayList<SlotModel>();
        Long start = startTime;
        while (start <= endTime - duration) {
            SlotModel slotModel = new SlotModel();
            slotModel.setStartTime(start);
            slotModel.setEndTime(start + duration);
            start = start + duration;
            slots.add(slotModel);
        } ;
        return slots;
    }
    
    @Override
    @Transactional
    public void insertIntoDB(SlotCategory serviceCategory) {
        /*Long currentTime = DateUtil.toCurrentUTCTimeStamp();
        if (serviceCategory == SlotCategory.LL_CATEGORY) {
            FileReader officeReader = null;
            try {
                officeReader = new FileReader(new File(
                        "D:\\rahul\\RTA\\tech doc\\LL TEST CENTER DETAILS.csv"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            if (officeReader == null) {
                return;
            }
            CSVReader csvReader = new CSVReader(officeReader);
            try {
                String[] row = null;
                while ((row = csvReader.readNext()) != null) {
                    boolean active = row[5].equals("Y") ? true : false;
                    if (active) {
                        RTAOfficeScheduleEntity scheduleEntity = new RTAOfficeScheduleEntity();
                        scheduleEntity.setStartTime(16200L);
                        scheduleEntity.setEndTime(30600L);
                        scheduleEntity.setIsEnabled(active);
                        scheduleEntity.setCreatedBy("admin");
                        scheduleEntity.setCreatedOn(currentTime);
                        scheduleEntity.setModifiedBy("admin");
                        scheduleEntity.setModifiedOn(currentTime);
                        scheduleEntity.setNumberOfSimultaneousSlots(0);
                        scheduleEntity.setRtaOfficeCode(row[2]);
                        scheduleEntity.setServiceCategory("LL");
                        RTAOfficeTestConfigEntity testConfig = new RTAOfficeTestConfigEntity();
                        testConfig.setCreatedBy("admin");
                        testConfig.setCreatedOn(currentTime);
                        testConfig.setDuration(3600L);
                        testConfig.setIsEnabled(Boolean.TRUE);
                        testConfig.setModifiedBy("admin");
                        testConfig.setModifiedOn(currentTime);
                        testConfig.setRtaOfficeSchedule(scheduleEntity);
                        testConfig.setSimulApplicationCount(Integer.parseInt(row[6]));
                        testConfig.setSlotServiceType(SlotServiceType.DRIVING_LICENCE_MACHINE_TEST);
                        rtaOfficeTestConfigDAO.saveOrUpdate(testConfig);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            } else if (serviceCategory == SlotCategory.DL_CATEGORY) {
                List<String> codes = rtaOfficeSchedulingDAO.getRTAOfficeScheduleCodes();
                for (String c : codes) {
                    RTAOfficeScheduleEntity scheduleEntity = new RTAOfficeScheduleEntity();
                    scheduleEntity.setStartTime(16200L);
                    scheduleEntity.setEndTime(30600L);
                    scheduleEntity.setIsEnabled(Boolean.TRUE);
                    scheduleEntity.setCreatedBy("admin");
                    scheduleEntity.setCreatedOn(currentTime);
                    scheduleEntity.setModifiedBy("admin");
                    scheduleEntity.setModifiedOn(currentTime);
                    scheduleEntity.setNumberOfSimultaneousSlots(0);
                    scheduleEntity.setRtaOfficeCode(c);
                    scheduleEntity.setServiceCategory("DL");
                    for (SlotServiceType sst : SlotServiceType.values()) {
                        if (sst == SlotServiceType.HMV_TEST || sst == SlotServiceType.THREE_FOUR_WHEELER_TEST || sst == SlotServiceType.TWO_WHEELER_TEST) {
                            RTAOfficeTestConfigEntity testConfig = new RTAOfficeTestConfigEntity();
                            testConfig.setCreatedBy("admin");
                            testConfig.setCreatedOn(currentTime);
                            testConfig.setDuration(3600L);
                            testConfig.setIsEnabled(Boolean.TRUE);
                            testConfig.setModifiedBy("admin");
                            testConfig.setModifiedOn(currentTime);
                            testConfig.setRtaOfficeSchedule(scheduleEntity);
                            if (sst == SlotServiceType.HMV_TEST) {
                                testConfig.setSimulApplicationCount(3);
                            } else if (sst == SlotServiceType.THREE_FOUR_WHEELER_TEST) {
                                testConfig.setSimulApplicationCount(10);
                            } else if (sst == SlotServiceType.TWO_WHEELER_TEST) {
                                testConfig.setSimulApplicationCount(14);
                            }
                            testConfig.setSlotServiceType(sst);
                            rtaOfficeTestConfigDAO.saveOrUpdate(testConfig);
                        }
                    }
                } 
            } else if (serviceCategory == SlotCategory.FITNESS_TESTS) {
                List<String> codes = rtaOfficeSchedulingDAO.getRTAOfficeScheduleCodes();
                for (String c : codes) {
                    RTAOfficeScheduleEntity scheduleEntity = new RTAOfficeScheduleEntity();
                    scheduleEntity.setStartTime(34200L);
                    scheduleEntity.setEndTime(41400L);
                    scheduleEntity.setIsEnabled(Boolean.TRUE);
                    scheduleEntity.setCreatedBy("admin");
                    scheduleEntity.setCreatedOn(currentTime);
                    scheduleEntity.setModifiedBy("admin");
                    scheduleEntity.setModifiedOn(currentTime);
                    scheduleEntity.setNumberOfSimultaneousSlots(0);
                    scheduleEntity.setRtaOfficeCode(c);
                    scheduleEntity.setServiceCategory(serviceCategory.getCode());
                    RTAOfficeTestConfigEntity testConfig = new RTAOfficeTestConfigEntity();
                    testConfig.setCreatedBy("admin");
                    testConfig.setCreatedOn(currentTime);
                    testConfig.setDuration(600L);
                    testConfig.setIsEnabled(Boolean.TRUE);
                    testConfig.setModifiedBy("admin");
                    testConfig.setModifiedOn(currentTime);
                    testConfig.setRtaOfficeSchedule(scheduleEntity);
                    testConfig.setSimulApplicationCount(6);
                    testConfig.setSlotServiceType(SlotServiceType.VEHICLE_INSPECTION_CHASSIS_ONLY);
                    rtaOfficeTestConfigDAO.saveOrUpdate(testConfig);
                }
            }*/
        }
}
