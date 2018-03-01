package org.rta.citizen.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "tax_type")
public class TaxTypeEntity extends BaseMasterEntity {

    private static final long serialVersionUID = -7741425246015526622L;

    @Id
    @Column(name = "tax_type_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tax_type_seq")
    @SequenceGenerator(name = "tax_type_seq", sequenceName = "tax_type_seq", allocationSize = 1)
    private Long taxTypeId;

    public Long getTaxTypeId() {
        return taxTypeId;
    }

    public void setTaxTypeId(Long taxTypeId) {
        this.taxTypeId = taxTypeId;
    }


}