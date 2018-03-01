package org.rta.citizen.common.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.rta.citizen.common.exception.UnauthorizedException;
import org.rta.citizen.common.model.vcr.VcrBookingData;
import org.rta.citizen.common.model.vcr.VcrService;
import org.rta.citizen.common.model.vcr.VcrServiceResponseModel;
import org.rta.citizen.common.utils.StringsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VCRServiceImpl implements VcrService {
    
	private static final Logger log = Logger.getLogger(VCRServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;
    
    @Value(value = "${service.vcr.host}")
    private String HOST;

    @Value(value = "${service.vcr.port}")
    private String PORT;

    @Value(value = "${service.vcr.path}")
    private String ROOT_URL;

    @Value(value = "${citizen.jwt.expiration}")
    private Long tokenExpiryTimeInSeconds;

    //http://59.162.46.199/rta_qa/vcr/getdetails/RC?docid=TN56B1699
    @Override
    public VcrServiceResponseModel<List<VcrBookingData>> getVCRForRCNumber(String prNumber) throws UnauthorizedException {
        ResponseEntity<List<VcrBookingData>> response = restTemplate.exchange(
                getRootURL().append("/getdetails/RC?docid=").append(prNumber).toString(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<VcrBookingData>>() {});
        HttpStatus httpStatus = response.getStatusCode();
        List<VcrBookingData> responseBody = null;
        if (httpStatus == HttpStatus.OK) {
            if (response.hasBody()) {
                responseBody = response.getBody();
            }
        }
        return new VcrServiceResponseModel<List<VcrBookingData>>(httpStatus, responseBody);
    }
    
    /*private StringBuilder getRootURL() {
        return new StringBuilder("http://").append(HOST).append("/").append(ROOT_URL);
    }*/
    
	private StringBuilder getRootURL() {
		StringBuilder url = new StringBuilder("http://").append(HOST);
		if (!StringsUtil.isNullOrEmpty(PORT)) {
		    url.append(":").append(PORT);
		}
		url.append("/").append(ROOT_URL);
		return url;
	}
    
}
