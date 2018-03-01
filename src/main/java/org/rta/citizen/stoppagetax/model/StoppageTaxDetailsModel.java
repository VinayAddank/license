package org.rta.citizen.stoppagetax.model;

import org.rta.citizen.common.model.AddressModel;

/**
 * @author sohan.maurya created on Jul 6, 2017
 *
 */
public class StoppageTaxDetailsModel extends AddressModel{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long stoppageDate;
	private String stoppageReason;
	
	public Long getStoppageDate() {
		return stoppageDate;
	}
	public void setStoppageDate(Long stoppageDate) {
		this.stoppageDate = stoppageDate;
	}
	public String getStoppageReason() {
		return stoppageReason;
	}
	public void setStoppageReason(String stoppageReason) {
		this.stoppageReason = stoppageReason;
	}
}
