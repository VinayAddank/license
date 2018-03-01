package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rta.citizen.common.enums.AttachmentFrom;
import org.rta.citizen.common.enums.Status;

/**
 *	@Author sohan.maurya created on Dec 9, 2016.
 */

@Entity
@Table(name = "application_attachments")
public class AttachmentEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "attachment_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_attachments_gen")
    @SequenceGenerator(name = "application_attachments_gen", sequenceName = "application_attachments_seq",
            allocationSize = 1)
    private Long attachmentDlId;

    @NotNull
    @Column(name = "filename", length = 200)
    private String fileName;

    @NotNull
    @Column(name = "source_dr")
    private String source;

    @NotNull
    @Column(name = "status", length = 2)
    private Status status;

    @NotNull
    @Column(name = "title", length = 200)
    private String attachmentTitle;

    @NotNull
    @Column(name = "attachment_from")
    private AttachmentFrom attachmentFrom;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private DocumentMasterEntity docTypes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationEntity applicationId;

    @Column(name = "user_type")
    private String userType;

    public Long getAttachmentDlId() {
        return attachmentDlId;
    }

    public void setAttachmentDlId(Long attachmentDlId) {
        this.attachmentDlId = attachmentDlId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAttachmentTitle() {
        return attachmentTitle;
    }

    public void setAttachmentTitle(String attachmentTitle) {
        this.attachmentTitle = attachmentTitle;
    }

    public AttachmentFrom getAttachmentFrom() {
        return attachmentFrom;
    }

    public void setAttachmentFrom(AttachmentFrom attachmentFrom) {
        this.attachmentFrom = attachmentFrom;
    }

    public DocumentMasterEntity getDocTypes() {
        return docTypes;
    }

    public void setDocTypes(DocumentMasterEntity docTypes) {
        this.docTypes = docTypes;
    }

    public ApplicationEntity getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(ApplicationEntity applicationId) {
        this.applicationId = applicationId;
    }

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
