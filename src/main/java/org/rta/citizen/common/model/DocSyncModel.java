/**
 * This model is used to sync the attachments uploaded in citizen to registration.
 */
package org.rta.citizen.common.model;

/**
 * @author arun.verma
 *
 */
public class DocSyncModel {

	private Integer attachmentFrom;
	private String title;
	private String filename;
	private String source;
	private Integer status;
	private Integer docTypeId;
	private String userTpe;

	public Integer getAttachmentFrom() {
		return attachmentFrom;
	}

	public void setAttachmentFrom(Integer attachmentFrom) {
		this.attachmentFrom = attachmentFrom;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getDocTypeId() {
		return docTypeId;
	}

	public void setDocTypeId(Integer docTypeId) {
		this.docTypeId = docTypeId;
	}

	public String getUserTpe() {
		return userTpe;
	}

	public void setUserTpe(String userTpe) {
		this.userTpe = userTpe;
	}

}