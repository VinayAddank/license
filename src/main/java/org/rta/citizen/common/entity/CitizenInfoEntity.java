/**
 * 
 */
package org.rta.citizen.common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.rta.citizen.common.enums.Gender;

/**
 * @author arun.verma
 *
 */
@Entity
@Table(name = "citizen_info")
public class CitizenInfoEntity implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1884113587862523666L;

    @Id
    @Column(name = "citizen_info_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "citizen_info_seq")
    @SequenceGenerator(name = "citizen_info_seq", sequenceName = "citizen_info_seq", allocationSize = 1)
    private Long citizenId;
    
    @Column(name = "aadhar_number")
    private String aadharNumber;
    
    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "middle_name")
    private String middleName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "father_name")
    private String fatherName;
    
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
