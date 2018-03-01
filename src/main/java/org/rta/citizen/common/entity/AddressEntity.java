/**
 * 
 */
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

/**
 * @author arun.verma
 *
 */
@Entity
@Table(name = "citizen_address")
public class AddressEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -3944651146293363539L;

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "citizen_address_seq")
    @SequenceGenerator(name = "citizen_address_seq", sequenceName = "citizen_address_seq", allocationSize = 1)
    private Long addressId;

    @Column(name = "door_no")
    private String doorNo;

    @Column(name = "street")
    private String streetName;

    @Column(name = "mandal_code")
    private Integer mandalCode;

    @Column(name = "town")
    private String townName;

    @Column(name = "district_code")
    private String districtCode;

    @Column(name = "state_code")
    private String stateCode;

    @Column(name = "country_code")
    private String countryCode;
    
    @Column(name = "pincode")
    private String pincode;

    @Column(name = "address_type", columnDefinition = "int default 1")
    private Integer addressType;

    @Column(name = "with_effect_from")
    private Long withEffectFrom;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationEntity applicationId;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "is_same_aadhar", columnDefinition = "boolean default false")
    private Boolean isSameAadhar;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getDoorNo() {
        return doorNo;
    }

    public void setDoorNo(String doorNo) {
        this.doorNo = doorNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public Integer getMandalCode() {
        return mandalCode;
    }

    public void setMandalCode(Integer mandalCode) {
        this.mandalCode = mandalCode;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }

    public Long getWithEffectFrom() {
        return withEffectFrom;
    }

    public void setWithEffectFrom(Long withEffectFrom) {
        this.withEffectFrom = withEffectFrom;
    }

    public ApplicationEntity getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(ApplicationEntity applicationId) {
        this.applicationId = applicationId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getIsSameAadhar() {
        return isSameAadhar;
    }

    public void setIsSameAadhar(Boolean isSameAadhar) {
        this.isSameAadhar = isSameAadhar;
    }
}
