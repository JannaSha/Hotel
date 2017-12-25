package com.billing.controllers;


import com.billing.controllers.BillingControllers;
import com.billing.models.Billing;
import com.billing.repos.BillingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


import static org.mockito.MockitoAnnotations.initMocks;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = {TestContext.class})
public class BillingControllerTest {

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private BillingControllers billingController;

    @Mock
    private BillingRepository billRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(billingController).build();
    }

    @Test
    public void shouldFindBillById() {
        Billing billing = new Billing(1L, 123456789, new BigDecimal(123));
        Mockito.when(billRepository.exists(1L)).thenReturn(true);
        Mockito.when(billRepository.findOne(1L)).thenReturn(billing);
        try {
            mockMvc.perform(get("/billing/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.cost", is(123)))
                    .andExpect(jsonPath("$.cartNumber", is(123456789)));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindBillById() {
        Mockito.when(billRepository.exists(1L)).thenReturn(false);
        try {
            mockMvc.perform(get("/billing/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindAllBillings() {
        List<Billing> billings = new LinkedList<>();
        billings.add(new Billing(1L, 123456789, new BigDecimal(123)));
        billings.add(new Billing(2L, 1234567891, new BigDecimal(123)));
        Mockito.when(billRepository.findAll()).thenReturn(billings);
        Integer page = 0;
        Integer size = 2;
        Mockito.when(billRepository.findAll(new PageRequest(page, size))).thenReturn(billings);
        try {
            mockMvc.perform(get("/billing/billings?page=0&size=2"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", billings, billingController.findAll(0, 2).getBody());
    }

    @Test
    public void shouldNotFindAllBillingsBadSize() {
        try {
            mockMvc.perform(get("/billing/billings?page=0&size=-2"))
                    .andExpect(status().isBadRequest());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindAllBillingsBadPage() {
        try {
            mockMvc.perform(get("/billing/billings?page=-1&size2"))
                    .andExpect(status().isBadRequest());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteBillingById() {
        Billing billing = new Billing(1L, 123456789, new BigDecimal(123));
        Mockito.when(billRepository.exists(1L)).thenReturn(true);
        Mockito.doNothing().when(billRepository).delete(1L);
        try {
            mockMvc.perform(delete(String.format("/billing/delete/%d", billing.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotDeleteBillById() {
        long billId = 1L;
        Mockito.when(billRepository.exists(billId)).thenReturn(false);
        try {
            mockMvc.perform(delete(String.format("/billing/delete/%d", billId)))
                    .andExpect(status().isNotFound());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    @Test
    public void shouldCreateBilling() {
        Billing billing = new Billing(1, 123456789L, new BigDecimal(123));
        String json = "";
        try {
            json = mapper.writeValueAsString(billing);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(billRepository.save(any(Billing.class))).thenReturn(billing);
        try {
            mockMvc.perform(post("/billing/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.cost", is(123)))
                    .andExpect(jsonPath("$.cartNumber", is(123456789)))
                    .andExpect(header().string("location", "http://localhost/billing/1"));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateBilling() {
        Billing billing = new Billing(1, 123456789L, new BigDecimal(123));
        String json = "";
        try {
            json = mapper.writeValueAsString(billing);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(billRepository.save(any(Billing.class))).thenReturn(null);
        try {
            mockMvc.perform(post("/billing/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }



}