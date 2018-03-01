package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *	@Author sohan.maurya created on Dec 9, 2016.
 */

@Entity
@Table(name = "document_master")
public class DocumentMasterEntity extends BaseEntity {

    private static final long serialVersionUID = -7274419569581833463L;

    @Id
    @Column(name = "document_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_master_gen")
    @SequenceGenerator(name = "document_master_gen", sequenceName = "document_master_seq", allocationSize = 1)
    private Integer docTypeId;

    @Column(name = "description", length = 100)
    private String name;

    @Column(name = "status")
    private Boolean isMandatory;

    @Column(name = "user_role", length = 100)
    private String roleName;

    public DocumentMasterEntity() {}

    public DocumentMasterEntity(Integer docTypeId) {
        this.docTypeId = docTypeId;
    }

    public Integer getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(Integer docTypeId) {
        this.docTypeId = docTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
