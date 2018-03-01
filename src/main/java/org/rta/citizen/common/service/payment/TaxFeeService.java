package org.rta.citizen.common.service.payment;

import org.rta.citizen.common.model.payment.DifferentialTaxFeeModel;
import org.rta.citizen.common.model.payment.TaxFeeModel;
import org.rta.citizen.common.model.payment.TaxRuleModel;

public interface TaxFeeService {

	public TaxFeeModel taxFeeCal(long sessionId , Boolean isDispatch , String aapNo , int quartelyType);
	
	public TaxFeeModel licenseTaxFeeCal(long sessionId);
	
	public TaxFeeModel userTaxFeeCal(long sessionId);
	
	public void testMailNdSMS(long sessionId , String status);
	
	/**
	 * this is using data syncing for differential tax and fee 
	 * @param prNumber
	 * @param applicationId
	 * @return
	 */
	public DifferentialTaxFeeModel saveOrUpdateDifferentialTaxFee(String prNumber, Long applicationId) ;
	
	public TaxRuleModel getTaxCal(String aapNo);
	
}
