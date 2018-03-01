package org.rta.citizen.licence.model;

import org.rta.citizen.common.model.AuthenticationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ForiegnCitizenAuthenticationModel extends AuthenticationModel {

	private static final long serialVersionUID = 5858479637955487914L;

	private String applicantName;
	private String dateofBirth;

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getDateofBirth() {
		return dateofBirth;
	}

	public void setDateofBirth(String dateofBirth) {
		this.dateofBirth = dateofBirth;
	}
}
