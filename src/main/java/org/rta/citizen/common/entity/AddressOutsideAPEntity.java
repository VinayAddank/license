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
@Table(name = "citizen_address_outside_ap")
public class AddressOutsideAPEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -8983505950090585448L;

    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_out_ap_seq")
    @SequenceGenerator(name = "address_out_ap_seq", sequenceName = "address_out_ap_seq", allocationSize = 1)
    private Long addressId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationEntity applicationId;

    @Column(name = "door_no")
    private String doorNo;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "pincode", length = 6)
    private String pincode;

    @Column(name = "mandal_name")
    private String mandalName;

    @Column(name = "town_name")
    private String townName;

    @Column(name = "district_name")
    private String districtName;

    @Column(name = "state_name")
    private String stateName;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "staus")
    private Boolean status;

    @Column(name = "with_effect_from")
    private Long withEffectFrom;

    @Column(name = "address_type", columnDefinition = "int default 1")
    private Integer addressType;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public ApplicationEntity getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(ApplicationEntity applicationId) {
        this.applicationId = applicationId;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getMandalName() {
        return mandalName;
    }

    public void setMandalName(String mandalName) {
        this.mandalName = mandalName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getWithEffectFrom() {
        return withEffectFrom;
    }

    public void setWithEffectFrom(Long withEffectFrom) {
        this.withEffectFrom = withEffectFrom;
    }

    public Integer getAddressType() {
        return addressType;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }
}
