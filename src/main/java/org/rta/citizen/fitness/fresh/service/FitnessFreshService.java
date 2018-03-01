package org.rta.citizen.fitness.fresh.service;

import org.rta.citizen.common.exception.NotFoundException;
import org.rta.citizen.common.model.FitnessDetailsModel;

public interface FitnessFreshService {
	
	public FitnessDetailsModel getFitnessDetails(String applicationNumber) throws NotFoundException;
}
