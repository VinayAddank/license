/**
 * 
 */
package org.rta.citizen.common.model;

import java.util.List;

import org.rta.citizen.common.model.payment.ApplicationTaxModel;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.rta.citizen.licence.model.updated.LearnersPermitDtlModel;
import org.rta.citizen.licence.model.updated.LicenseHolderPermitDetails;
import org.rta.citizen.stoppagetax.model.StoppageTaxDetailsModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author arun.verma
 *
 */
public class GeneralDetails {

    private CustomerDetailsRequestModel customerDetails;
    private VehicleDetailsRequestModel vehicleDetails;
    private List<ChallanDetailsModel> challanDetailsList;
    private List<CrimeDetailsModel> crimeDetailsList;
    private ApplicationTaxModel taxDetails;
    private List<PermitDetailsModel> permitDetails;
    private FitnessDetailsModel fitnessDetails;
    private FinanceModel financeDetails;
    private InsuranceDetailsModel insuranceDetails;
    private PucDetailsModel pucDetails;
    private NocDetails nocDetails;
    private SuspendedRCNumberModel suspensionDetails;
    private StoppageTaxDetailsModel stoppageTaxDetails;
    
    //for DL/LLR
    private LicenseHolderPermitDetails licenseHolderDtls;
    private List<LearnersPermitDtlModel> learnersPermitDtlList;
    
    public CustomerDetailsRequestModel getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(CustomerDetailsRequestModel customerDetails) {
        this.customerDetails = customerDetails;
    }

    public VehicleDetailsRequestModel getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(VehicleDetailsRequestModel vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public List<CrimeDetailsModel> getCrimeDetailsList() {
        return crimeDetailsList;
    }

    public void setCrimeDetailsList(List<CrimeDetailsModel> crimeDetailsList) {
        this.crimeDetailsList = crimeDetailsList;
    }

    public ApplicationTaxModel getTaxDetails() {
        return taxDetails;
    }

    public void setTaxDetails(ApplicationTaxModel taxDetails) {
        this.taxDetails = taxDetails;
    }

    public FitnessDetailsModel getFitnessDetails() {
        return fitnessDetails;
    }

    public void setFitnessDetails(FitnessDetailsModel fitnessDetails) {
        this.fitnessDetails = fitnessDetails;
    }

    public FinanceModel getFinanceDetails() {
        return financeDetails;
    }

    public void setFinanceDetails(FinanceModel financeDetails) {
        this.financeDetails = financeDetails;
    }

    public InsuranceDetailsModel getInsuranceDetails() {
        return insuranceDetails;
    }

    public void setInsuranceDetails(InsuranceDetailsModel insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }

    public PucDetailsModel getPucDetails() {
        return pucDetails;
    }

    public void setPucDetails(PucDetailsModel pucDetails) {
        this.pucDetails = pucDetails;
    }

    public NocDetails getNocDetails() {
        return nocDetails;
    }

    public void setNocDetails(NocDetails nocDetails) {
        this.nocDetails = nocDetails;
    }
    
    public LicenseHolderPermitDetails getLicenseHolderDtls() {
		return licenseHolderDtls;
	}

	public void setLicenseHolderDtls(LicenseHolderPermitDetails licenseHolderDtls) {
		this.licenseHolderDtls = licenseHolderDtls;
	}

	public List<LearnersPermitDtlModel> getLearnersPermitDtlList() {
		return learnersPermitDtlList;
	}

	public void setLearnersPermitDtlList(List<LearnersPermitDtlModel> learnersPermitDtlList) {
		this.learnersPermitDtlList = learnersPermitDtlList;
	}

    public List<PermitDetailsModel> getPermitDetails() {
        return permitDetails;
    }

    public void setPermitDetails(List<PermitDetailsModel> permitDetails) {
        this.permitDetails = permitDetails;
    }
	
	public List<ChallanDetailsModel> getChallanDetailsList() {
		return challanDetailsList;
	}

	public void setChallanDetailsList(List<ChallanDetailsModel> challanDetailsList) {
		this.challanDetailsList = challanDetailsList;
	}
	
	public SuspendedRCNumberModel getSuspensionDetails() {
		return suspensionDetails;
	}

	public void setSuspensionDetails(SuspendedRCNumberModel suspensionDetails) {
		this.suspensionDetails = suspensionDetails;
	}
	
	public StoppageTaxDetailsModel getStoppageTaxDetails() {
		return stoppageTaxDetails;
	}

	public void setStoppageTaxDetails(StoppageTaxDetailsModel stoppageTaxDetails) {
		this.stoppageTaxDetails = stoppageTaxDetails;
	}

	@JsonIgnore
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        
        private CustomerDetailsRequestModel customerDetails;
        private VehicleDetailsRequestModel vehicleDetails;
        private List<ChallanDetailsModel> challanDetailsList;
        private List<CrimeDetailsModel> crimeDetailsList;
        private FitnessDetailsModel fitnessDetails;
        private FinanceModel financeDetails;
        private InsuranceDetailsModel insuranceDetails;
        private PucDetailsModel pucDetails;
        private NocDetails nocDetails;
        private ApplicationTaxModel taxDetails;
        private List<PermitDetailsModel> permitDetails;
        private SuspendedRCNumberModel suspensionDetails;
        private StoppageTaxDetailsModel stoppageTaxDetails;
        
        //for DL/LLR
        private LicenseHolderPermitDetails licenseHolderDtls;
        private List<LearnersPermitDtlModel> learnersPermitDtlList;
        
        private Builder() {
            
        }
        
        public Builder add(CustomerDetailsRequestModel customerDetailsRequestModel) {
            this.customerDetails = customerDetailsRequestModel;
            return this;
        }
        
        public Builder add(VehicleDetailsRequestModel vehicleDetails) {
            this.vehicleDetails = vehicleDetails;
            return this;
        }
        
        public Builder addChallanDetails(List<ChallanDetailsModel> challanDetails) {
            this.challanDetailsList = challanDetails;
            return this;
        }
        
        public Builder addCrimeDetails(List<CrimeDetailsModel> crimeDetails) {
            this.crimeDetailsList = crimeDetails;
            return this;
        }
        
        public Builder add(ApplicationTaxModel tax) {
            this.taxDetails = tax;
            return this;
        }
        
        public Builder add(List<PermitDetailsModel> permitDetailsModel) {
            this.permitDetails = permitDetailsModel;
            return this;
        }
        
        public Builder add(FitnessDetailsModel fitnessDetails) {
            this.fitnessDetails = fitnessDetails;
            return this;
        }
        
        public Builder add(FinanceModel financeDetails) {
            this.financeDetails = financeDetails;
            return this;
        }
        
        public Builder add(InsuranceDetailsModel insuranceDetails) {
            this.insuranceDetails = insuranceDetails;
            return this;
        }
        
        public Builder add(PucDetailsModel pucDetails) {
            this.pucDetails = pucDetails;
            return this;
        }
        
        public Builder add(NocDetails nocDetails) {
            this.nocDetails = nocDetails;
            return this;
        }
        
        public Builder add(LicenseHolderPermitDetails licenseHolderDtls) {
            this.licenseHolderDtls = licenseHolderDtls;
            return this;
        }
        
        public Builder addLearnersPermitDtl(List<LearnersPermitDtlModel> learnersPermitDtlList) {
            this.learnersPermitDtlList = learnersPermitDtlList;
            return this;
        }
        
        public Builder add(SuspendedRCNumberModel suspensionDetails) {
            this.suspensionDetails = suspensionDetails;
            return this;
        }
        
        public Builder add(StoppageTaxDetailsModel stoppageTaxDetails){
        	this.stoppageTaxDetails = stoppageTaxDetails;
        	return this;
        }
        
        public GeneralDetails build() {
            GeneralDetails generalDetails = new GeneralDetails();
            if (!ObjectsUtil.isNull(customerDetails)) {
                generalDetails.setCustomerDetails(customerDetails);
            }
            if (!ObjectsUtil.isNull(vehicleDetails)) {
                generalDetails.setVehicleDetails(vehicleDetails);
            }
            if (!ObjectsUtil.isNull(challanDetailsList)) {
                generalDetails.setChallanDetailsList(challanDetailsList);
            }
            if (!ObjectsUtil.isNull(crimeDetailsList)) {
                generalDetails.setCrimeDetailsList(crimeDetailsList);
            }
            if (!ObjectsUtil.isNull(taxDetails)) {
                generalDetails.setTaxDetails(taxDetails);
            }
            if (!ObjectsUtil.isNull(permitDetails)) {
                generalDetails.setPermitDetails(permitDetails);
            }
            if (!ObjectsUtil.isNull(fitnessDetails)) {
                generalDetails.setFitnessDetails(fitnessDetails);
            }
            if (!ObjectsUtil.isNull(financeDetails)) {
                generalDetails.setFinanceDetails(financeDetails);
            }
            if (!ObjectsUtil.isNull(insuranceDetails)) {
                generalDetails.setInsuranceDetails(insuranceDetails);
            }
            if (!ObjectsUtil.isNull(pucDetails)) {
                generalDetails.setPucDetails(pucDetails);
            }
            if (!ObjectsUtil.isNull(nocDetails)) {
                generalDetails.setNocDetails(nocDetails);
            }
            if (!ObjectsUtil.isNull(licenseHolderDtls)) {
                generalDetails.setLicenseHolderDtls(licenseHolderDtls);
            }
            if (!ObjectsUtil.isNull(learnersPermitDtlList)) {
                generalDetails.setLearnersPermitDtlList(learnersPermitDtlList);
            }
            if (!ObjectsUtil.isNull(suspensionDetails)) {
                generalDetails.setSuspensionDetails(suspensionDetails);
            }
            if(!ObjectsUtil.isNull(stoppageTaxDetails)){
            	generalDetails.setStoppageTaxDetails(stoppageTaxDetails);
            }
            return generalDetails;
        }
        
    }

}
