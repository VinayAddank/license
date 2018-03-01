/**
 * 
 */
package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.rta.citizen.common.enums.Status;

/**
 * @author arun.verma
 *
 */
@Entity
@Table(name = "application_form_data")
// @Subselect("select * from application_form_data")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class ApplicationFormDataEntity extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -3277274009494102569L;

    @Id
    @Column(name = "data_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "data_id_seq")
    @SequenceGenerator(name = "data_id_seq", sequenceName = "data_id_seq", allocationSize = 1)
    private Long dataId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id")
    private ApplicationEntity applicationEntity;

    /**
     * In db form_data is json
     */
    @Type(type = "StringJsonObject")
    @Column(name = "form_data")
    private String formData;

    @Column(name = "form_code")
    private String formCode;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public ApplicationEntity getApplicationEntity() {
        return applicationEntity;
    }

    public void setApplicationEntity(ApplicationEntity applicationEntity) {
        this.applicationEntity = applicationEntity;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
