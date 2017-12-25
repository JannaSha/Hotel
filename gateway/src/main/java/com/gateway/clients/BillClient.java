package com.gateway.clients;

import com.gateway.models.Billing;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class BillClient {

    private String serviceUrl = "http://localhost:3333/billing";
    private RestTemplate restTemplate;

    public BillClient() {
        restTemplate = new RestTemplate();
    }

    public ResponseEntity<Billing> findOne(long id) {
        return restTemplate.getForEntity(serviceUrl + String.format("/%d", id), Billing.class);
    }
    public ResponseEntity<Billing> createBill(Billing bill) {
        return restTemplate.exchange(serviceUrl + "/create", HttpMethod.POST,
                new HttpEntity<>(bill),Billing.class);
    }

    public Void delete(long id) {
        restTemplate.delete(serviceUrl + String.format("/delete/%d", id));
        return null;
    }

}
