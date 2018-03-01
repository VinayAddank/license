package org.rta.citizen.aadharseeding.rc.service;

import java.util.Map;

import org.rta.citizen.common.model.ResponseModel;

public interface RCASService {
	
	public Map<String, Object> getMatchDataBwAadhaarAndRC(String applicationNo);
	
	public ResponseModel<String> aadhaarSeedingWithSystem(Long applicationId, String prNumber, String adhaarNumber);
}
