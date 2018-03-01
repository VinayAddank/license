/**
 * 
 */
package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.licence.entity.updated.BaseLicenseEntity;

/**
 * @author neeraj.maletia
 *
 */
@Entity
@Table(name = "rta_office_ipaddress")
public class RtaOfficeIPAddressEntity extends BaseLicenseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -3944651146293363539L;
    
    @Id
    @Column(name = "office_ipaddress_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_ipaddress_seq")
    @SequenceGenerator(name = "office_ipaddress_seq", sequenceName = "office_ipaddress_seq", allocationSize = 1)
    private Long officeIPaddressId;
    
    @Column(name = "office_code", length = 255)
    private String officeCode;
    
    @Column(name = "ipaddress", length = 50)
    private String ipAddress;
    
    @Column(name = "status", length = 10)
    private String status;
    
    @Column(name = "remarks", length = 255)
    private String remarks;

	public Long getOfficeIPaddressId() {
		return officeIPaddressId;
	}

	public void setOfficeIPaddressId(Long officeIPaddressId) {
		this.officeIPaddressId = officeIPaddressId;
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

    
}
