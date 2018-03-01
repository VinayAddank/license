package org.rta.citizen.common.service;

import java.util.Arrays;
import java.util.List;

import org.rta.citizen.addresschange.model.AddressChangeModel;
import org.rta.citizen.common.model.DocPermissionModel;
import org.rta.citizen.common.model.DocTypesModel;
import org.rta.citizen.common.model.RegistrationServiceResponseModel;
import org.rta.citizen.common.model.SaveUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class RuleEngineServiceImpl implements RuleEngineService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${drools.root.url}")
    private String droolsRootURL;
    
    @Override
    public RegistrationServiceResponseModel<List<DocTypesModel>> getAttachments(DocPermissionModel model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DocPermissionModel> httpEntity = new HttpEntity<DocPermissionModel>(model, headers);
        ResponseEntity<DocTypesModel[]> response = null;
        HttpStatus httpStatus = null;
        try{
            response = restTemplate.exchange( new StringBuilder(droolsRootURL).append("/citdocuments").toString(), HttpMethod.PUT, httpEntity, DocTypesModel[].class);
            httpStatus = response.getStatusCode();
        } catch(HttpStatusCodeException ex){
            httpStatus = ex.getStatusCode();
        }       
        List<DocTypesModel> responseBody = null;
        if (httpStatus == HttpStatus.OK) {
            if (response.hasBody()) {
                responseBody = Arrays.asList(response.getBody());
            }
        }
        return new RegistrationServiceResponseModel<List<DocTypesModel>>(httpStatus, responseBody);
    }

}
