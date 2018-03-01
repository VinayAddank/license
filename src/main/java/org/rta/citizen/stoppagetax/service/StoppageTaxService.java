package org.rta.citizen.stoppagetax.service;

import java.util.List;

import org.rta.citizen.common.model.CitizenApplicationModel;
import org.rta.citizen.common.model.ResponseModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;
import org.rta.citizen.stoppagetax.model.StoppageTaxReportModel;

/**
 * @author sohan.maurya created on Jul 5, 2017
 *
 */

public interface StoppageTaxService {
	
	public ResponseModel<String> saveOrUpdateStoppageTax(String prNumber, Long applicationId) ;

	public ResponseModel<String> stoppageTaxReportSync( StoppageTaxReportModel model, String userName, Long userId);

	public List<CitizenApplicationModel> getInspectionOpenApplications(Long userId, String applicationNumber);

	public Integer getInspectionOpenApplicationCount(Long userId);

	public List<StoppageTaxReportModel> getStoppageTaxReport(String applicationNo);

	public StoppageTaxDetailsModel getStoppageTaxDetails(String applicationNo);
	
}
