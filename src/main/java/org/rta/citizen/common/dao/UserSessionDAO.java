package org.rta.citizen.common.dao;

import java.util.List;

import org.rta.citizen.common.entity.UserSessionEntity;
import org.rta.citizen.common.enums.KeyType;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.enums.ServiceType;
import org.rta.citizen.common.enums.Status;

public interface UserSessionDAO extends GenericDAO<UserSessionEntity> {

    List<UserSessionEntity> getLoginHistory(String aadharNumber, Long fromTimestamp, Long toTimestamp, String uniqueKey, KeyType keyType);
    
    UserSessionEntity getActiveSession(String aadharNumber, String uniqueKey, KeyType keyType);
    
    UserSessionEntity getActiveSession(String aadharNumber, String uniqueKey, KeyType keyType, ServiceType serviceType);
    
    UserSessionEntity getUserSession(String aadharNumber, String uniqueKey, KeyType keyType, ServiceType serviceType,
            Status status);
    
    public UserSessionEntity getUserSession(Long sessionId);
    
    public List<UserSessionEntity> getAppliedSessions(String aadharNo, String serviceCode);

    public UserSessionEntity getLastAppSessionByUniqueKey(String uniqueKey);

    public List<UserSessionEntity> getAppliedSessions(String aadharNo);

    public List<UserSessionEntity> getApprovedAppSessions(String aadharNo);

    public List<UserSessionEntity> getRejectedAppSessions(String aadharNo, String serviceCode);

    public UserSessionEntity getLatestUserSession(String aadharNumber, String uniqueKey, KeyType keyType, ServiceType serviceType, Status status);
    
    public UserSessionEntity getUserSessionByUniqueKey(String uniqueKey, ServiceType serviceType);

    public UserSessionEntity getUserSessions(String aadharNo, String uniqueKey, Status status, ServiceType serviceType);

    public UserSessionEntity getLastTheftUserSession(String aadharNumber, String uniqueKey, KeyType keyType);

    public UserSessionEntity getTheftUserSession(String uniqueKey, KeyType keyType);

	public UserSessionEntity getAppliedSessionsForLLR(String aadharNo);
	
	public UserSessionEntity getLastSessionForFitnessReInspection(String uniqueKey);
	
	public UserSessionEntity getLastRejectedApprovedSession(String uniqueKey, List<String> serviceTypes, List<Integer> status);
    
}