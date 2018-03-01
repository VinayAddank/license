/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author arun.verma
 *
 */

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class RTAOfficeModel {

    private Long rtaOfficeId;
    private String name;
    private String code;
    private String phone;
    private String mobile;
    private String fax;
    private String email;
    private Boolean status;
    private String officeType;
    private String seriesMapid;
    private List<MandalModel> mandal;
    private String address;

    public Long getRtaOfficeId() {
        return rtaOfficeId;
    }

    public void setRtaOfficeId(Long rtaOfficeId) {
        this.rtaOfficeId = rtaOfficeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getOfficeType() {
        return officeType;
    }

    public void setOfficeType(String officeType) {
        this.officeType = officeType;
    }

    public String getSeriesMapid() {
        return seriesMapid;
    }

    public void setSeriesMapid(String seriesMapid) {
        this.seriesMapid = seriesMapid;
    }

    public List<MandalModel> getMandal() {
        return mandal;
    }

    public void setMandal(List<MandalModel> mandal) {
        this.mandal = mandal;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
