/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;

import org.rta.citizen.common.enums.Status;

/**
 * @author admin
 *
 */
public class AppActionModel {

	private String applicationNumber;
	private List<DocActionModel> docActions;
	private Status appStatus;
	private CommentModel appComment;

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public List<DocActionModel> getDocActions() {
		return docActions;
	}

	public void setDocActions(List<DocActionModel> docActions) {
		this.docActions = docActions;
	}

	public Status getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(Status appStatus) {
		this.appStatus = appStatus;
	}

	public CommentModel getAppComment() {
		return appComment;
	}

	public void setAppComment(CommentModel appComment) {
		this.appComment = appComment;
	}

}
