/**
 * 
 */
package org.rta.citizen.ownershiptransfer.model;

import org.rta.citizen.common.model.ApplicantModel;
import org.rta.citizen.common.model.ApplicationFormDataModel;

/**
 * @author admin
 *
 */
public class BuyerModel {
	private ApplicantModel applicant;
	private ApplicationFormDataModel form;

	public ApplicantModel getApplicant() {
		return applicant;
	}

	public void setApplicant(ApplicantModel applicant) {
		this.applicant = applicant;
	}

	public ApplicationFormDataModel getForm() {
		return form;
	}

	public void setForm(ApplicationFormDataModel form) {
		this.form = form;
	}

}
