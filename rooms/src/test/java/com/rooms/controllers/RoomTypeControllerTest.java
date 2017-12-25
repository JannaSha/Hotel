package com.rooms.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.rooms.controllers.RoomTypeController;
import com.rooms.models.RoomType;
import com.rooms.repo.RoomsTypesRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


import static org.mockito.MockitoAnnotations.initMocks;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = {TestContext.class})
public class RoomTypeControllerTest {

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private RoomTypeController roomTypeController;

    @Mock
    private RoomsTypesRepository roomsTypesRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomTypeController).build();
    }

    @Test
    public void shouldFindRoomTypeById() {
        RoomType roomType = new RoomType(15, 15, "first",
                "description", new BigDecimal(123), 2);
        Mockito.when(roomsTypesRepository.exists(6L)).thenReturn(true);
        Mockito.when(roomsTypesRepository.findOne(roomType.getId())).thenReturn(roomType);
        try {
            mockMvc.perform(get("/roomtype/6"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.amountRooms", is(15)))
                    .andExpect(jsonPath("$.amountVacantRooms", is(15)))
                    .andExpect(jsonPath("$.description", is("description")))
                    .andExpect(jsonPath("$.amountRoomines", is(2)))
                    .andExpect(jsonPath("$.price", is(123)))
                    .andExpect(jsonPath("$.typeName", is("first")));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindRoomById() {
        Mockito.when(roomsTypesRepository.exists(6L)).thenReturn(false);
        try {
            mockMvc.perform(get("/roomtype/6"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindAllRooms() {
        List<RoomType> roomTypes = new LinkedList<>();
        roomTypes.add(new RoomType(15, 15, "first",
                "description", new BigDecimal(123), 2));
        roomTypes.add(new RoomType(15, 15, "first",
                "description", new BigDecimal(123), 2));
        Integer page = 0;
        Integer size = 2;
        Mockito.when(roomsTypesRepository.findAll(new PageRequest(page, size))).thenReturn(roomTypes);
        try {
            mockMvc.perform(get(String.format("/roomtype/roomtypes?page=%d&size=%d", page, size)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", roomTypes, roomTypeController.findAllRoomsType(page, size).getBody());
    }

    @Test
    public void shouldNotFindAllRoomTypesBadPage() {
        Integer page = -1;
        Integer size = 2;
        try {
            mockMvc.perform(get(String.format("/roomtype/roomtypes?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindAllRoomTypesBadSize() {
        Integer page = 1;
        Integer size = -2;
        try {
            mockMvc.perform(get(String.format("/roomtype/roomtypes?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotDeleteRoomTypeById() {
        long roomId = 1L;
        Mockito.when(roomsTypesRepository.exists(roomId)).thenReturn(false);
        try {
            mockMvc.perform(delete(String.format("/roomtype/delete/%d", roomId)))
                    .andExpect(status().isNotFound());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteUserById() {
        long roomId = 1L;
        Mockito.when(roomsTypesRepository.exists(roomId)).thenReturn(true);
        Mockito.doNothing().when(roomsTypesRepository).delete(roomId);
        try {
            mockMvc.perform(delete(String.format("/roomtype/delete/%d", roomId)))
                    .andExpect(status().isOk());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldCreateRoomType() {
        RoomType roomType = new RoomType(1, 15, 15, "first",
                "description", new BigDecimal(123), 2);
        String json = "";
        try {
            json = mapper.writeValueAsString(roomType);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(roomsTypesRepository.save(any(RoomType.class))).thenReturn(roomType);
        try {
            mockMvc.perform(post("/roomtype/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.amountRooms", is(15)))
                    .andExpect(jsonPath("$.amountVacantRooms", is(15)))
                    .andExpect(jsonPath("$.description", is("description")))
                    .andExpect(jsonPath("$.amountRoomines", is(2)))
                    .andExpect(jsonPath("$.price", is(123)))
                    .andExpect(jsonPath("$.typeName", is("first")))
                    .andExpect(header().string("location", "http://localhost/roomtype/1"));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateRoomType() {
        RoomType roomType = new RoomType(1, 15, 15, "first",
                "description", new BigDecimal(123), 2);
        String json = "";
        try {
            json = mapper.writeValueAsString(roomType);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(roomsTypesRepository.save(any(RoomType.class))).thenReturn(null);
        try {
            mockMvc.perform(post("/roomtype/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}