package org.rta.citizen.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Nikhilesh.Tiwari
 *
 */
@JsonInclude(Include.NON_NULL)
public class LegalHierModel {

    private Integer numberOfLegalHier;
    private List<LegalHierDetailsModel> legalHierDetailsModel;

    public Integer getNumberOfLegalHier() {
        return numberOfLegalHier;
    }

    public void setNumberOfLegalHier(Integer numberOfLegalHier) {
        this.numberOfLegalHier = numberOfLegalHier;
    }

    public List<LegalHierDetailsModel> getLegalHierDetailsModel() {
        return legalHierDetailsModel;
    }

    public void setLegalHierDetailsModel(List<LegalHierDetailsModel> legalHierDetailsModel) {
        this.legalHierDetailsModel = legalHierDetailsModel;
    }

}
