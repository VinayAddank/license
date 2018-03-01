package org.rta.citizen.common.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.rta.citizen.common.enums.AttachmentFrom;
import org.rta.citizen.common.enums.Status;
import org.rta.citizen.common.enums.ServiceType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * this model is use for store and get url documents, and these are applicant documents.. uploaded
 * by dealer on other server
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class AttachmentModel {


    private Long attachmentDlId;

    @NotNull(message = " id is Missing")
    private Integer id;

    @NotNull(message = " File Name is Missing")
    private String fileName;

    @NotNull(message = " Attachment Title is Missing")
    private String attachmentTitle;

    @NotNull(message = " Source is Missing")
    private String source;

    private Status status;
    private String vehicleRcId;
    private String chassisNumber;
    private ServiceType serviecType;
    private Long sessionId;

    @NotNull(message = " attachment device is Missing(eg. MOBILE, DESKTOP)")
    private AttachmentFrom attachmentFrom;

    private String userName;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

	public String getVehicleRcId() {
		return vehicleRcId;
	}

	public void setVehicleRcId(String vehicleRcId) {
		this.vehicleRcId = vehicleRcId;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public ServiceType getServiecType() {
		return serviecType;
	}

	public void setServiecType(ServiceType serviecType) {
		this.serviecType = serviecType;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}


}
