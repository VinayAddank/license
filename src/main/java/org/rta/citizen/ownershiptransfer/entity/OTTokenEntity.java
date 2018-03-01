/**
 * 
 */
package org.rta.citizen.ownershiptransfer.entity;

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

import org.rta.citizen.common.entity.ApplicationEntity;
import org.rta.citizen.common.entity.BaseEntity;

/**
 * @author arun.verma
 *
 */
@Entity
@Table(name = "ot_token")
public class OTTokenEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -7424552801653800953L;

    @Id
    @Column(name = "token_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ot_token_seq")
    @SequenceGenerator(name = "ot_token_seq", sequenceName = "ot_token_seq", allocationSize = 1)
    private Long tokenId;

    @Column(name = "token_number")
    private String tokenNumber;

    @Column(name = "generator_name")
    private String generatorName;

    @Column(name = "generator_aadhaar_number")
    private String generatorAadhaarNumber;

    @Column(name = "is_claimed", columnDefinition = "boolean default false")
    private Boolean isClaimed;

    @Column(name = "claimant_name")
    private String claimantName;

    @Column(name = "claimant_aadhaar_number")
    private String claimantAadhaarNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private ApplicationEntity applicationEntity;
    
    @Column(name = "generator_ip", length = 50)
    private String generatorIp;
    
    @Column(name = "claimant_ip", length = 50)
    private String claimantIp;

    @Column(name = "ownership_type")
    private Integer ownershipType;


    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }

    public String getGeneratorAadhaarNumber() {
        return generatorAadhaarNumber;
    }

    public void setGeneratorAadhaarNumber(String generatorAadhaarNumber) {
        this.generatorAadhaarNumber = generatorAadhaarNumber;
    }

    public Boolean getIsClaimed() {
        return isClaimed;
    }

    public void setIsClaimed(Boolean isClaimed) {
        this.isClaimed = isClaimed;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantAadhaarNumber() {
        return claimantAadhaarNumber;
    }

    public void setClaimantAadhaarNumber(String claimantAadhaarNumber) {
        this.claimantAadhaarNumber = claimantAadhaarNumber;
    }

    public ApplicationEntity getApplicationEntity() {
        return applicationEntity;
    }

    public void setApplicationEntity(ApplicationEntity applicationEntity) {
        this.applicationEntity = applicationEntity;
    }

    public String getGeneratorIp() {
        return generatorIp;
    }

    public void setGeneratorIp(String generatorIp) {
        this.generatorIp = generatorIp;
    }

    public String getClaimantIp() {
        return claimantIp;
    }

    public void setClaimantIp(String claimantIp) {
        this.claimantIp = claimantIp;
    }

    public Integer getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(Integer ownershipType) {
        this.ownershipType = ownershipType;
    }

}
