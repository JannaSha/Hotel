package com.gateway.clients;

import com.gateway.models.Billing;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class BillClient extends TokenClient {

    private String serviceUrl = "http://localhost:3333/billing";
    private String tokenUrl = "http://localhost:3333/billing/token";
    private RestTemplate restTemplate;


    public BillClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<Billing> findOne(long id) {
        if (setToken( tokenUrl, restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/%d", id), HttpMethod.GET, entity, Billing.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    public ResponseEntity<Billing> createBill(Billing bill) {
        if (setToken(tokenUrl, restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<Billing> entity = new HttpEntity<>(bill, appHashHeaders);
            return restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST,
                    entity, Billing.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ResponseEntity<Billing> delete(long id) {
        if (setToken(tokenUrl, restTemplate)) {
            HttpHeaders appHashHeaders = new HttpHeaders();
            appHashHeaders.add("token", token);
            HttpEntity<String> entity = new HttpEntity<>("parameters", appHashHeaders);
            return restTemplate.exchange(serviceUrl + String.format("/delete/%d", id), HttpMethod.DELETE,
                    entity, Billing.class);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
