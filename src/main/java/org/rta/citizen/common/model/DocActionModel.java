/**
 * 
 */
package org.rta.citizen.common.model;

import org.rta.citizen.common.enums.Status;

/**
 * @author admin
 *
 */
public class DocActionModel {

	private DocTypesModel docModel;
	private Status status;
	private CommentModel comment;

	public DocTypesModel getDocModel() {
		return docModel;
	}

	public void setDocModel(DocTypesModel docModel) {
		this.docModel = docModel;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public CommentModel getComment() {
		return comment;
	}

	public void setComment(CommentModel comment) {
		this.comment = comment;
	}

}
