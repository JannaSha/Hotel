package com.rooms.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.rooms.controllers.RoomController;
import com.rooms.models.Room;
import com.rooms.repo.RoomsRepository;
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
import java.util.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
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
public class RoomsControllerTests {

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private RoomsTypesRepository roomsTypesRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    public void shouldFindRoomById() {
        Room room = new Room(100, 1, true);
        Mockito.when(roomsRepository.exists(room.getId())).thenReturn(true);
        Mockito.when(roomsRepository.findOne(room.getId())).thenReturn(room);
        try {
            mockMvc.perform(get("/room/100"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(100)))
                    .andExpect(jsonPath("$.roomType", is(1)))
                    .andExpect(jsonPath("$.vacant", is(true)));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindRoomById() {
        Mockito.when(roomsRepository.exists(100L)).thenReturn(false);
        try {
            mockMvc.perform(get("/room/100"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    @Test
    public void shouldFindAllRooms() {
        List<Room> rooms = new LinkedList<>();
        rooms.add(new Room(100L,1, true));
        rooms.add(new Room(200L,2, false ));
        Integer page = 0;
        Integer size = 2;
        Mockito.when(roomsRepository.findAll(new PageRequest(page, size))).thenReturn(rooms);
        try {
            mockMvc.perform(get(String.format("/room/rooms?page=%d&size=%d", page, size)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", rooms, roomController.findAll(page, size, null).getBody());
    }

    @Test
    public void shouldNotFindAllRoomsBadPage() {
        Integer page = -1;
        Integer size = 2;
        try {
            mockMvc.perform(get(String.format("/room/rooms?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindAllRoomsBadSize() {
        Integer page = 1;
        Integer size = -2;
        try {
            mockMvc.perform(get(String.format("/room/rooms?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindRoomsByTypeId() {
        List<Room> rooms = new LinkedList<>();
        rooms.add(new Room(100L,1, true));
        Mockito.when(roomsTypesRepository.exists(1L)).thenReturn(true);
        Mockito.when(roomsRepository.findByRoomTypeAndVacant(1L, true)).thenReturn(rooms);
        try {
            mockMvc.perform(get("/room/roomtype/1?vacant=true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", rooms, roomController.findByType(1, true, null).getBody());

    }

    @Test
    public void shouldNotFindRoomsByTypeId() {
        Mockito.when(roomsTypesRepository.exists(1L)).thenReturn(false);
        try {
            mockMvc.perform(get("/room/roomtype/1?vacant=false"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteRoomById() {
        Room room = new Room(100, 1, true);
        Mockito.when(roomsRepository.exists(room.getId())).thenReturn(true);
        Mockito.doNothing().when(roomsRepository).delete(room.getId());
        try {
            mockMvc.perform(delete(String.format("/room/delete/%d", room.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotDeleteRoomById() {
        long roomId = 1L;
        Mockito.when(roomsRepository.exists(roomId)).thenReturn(false);
        try {
            mockMvc.perform(delete(String.format("/room/delete/%d", roomId)))
                    .andExpect(status().isNotFound());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    @Test
    public void shouldCreateRoom() {
        Room room = new Room(100, 1, true);
        String json = "";
        try {
            json = mapper.writeValueAsString(room);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(roomsRepository.exists(100L)).thenReturn(false);
        Mockito.when(roomsRepository.save(any(Room.class))).thenReturn(room);
        Mockito.when(roomsTypesRepository.exists(1L)).thenReturn(true);
        try {
            mockMvc.perform(post("/room/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(100)))
                    .andExpect(jsonPath("$.roomType", is(1)))
                    .andExpect(jsonPath("$.vacant", is(true)))
                    .andExpect(header().string("location", "http://localhost/room/100"));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateRoomRoomExist() {
        Room room = new Room(100, 1, true);
        String json = "";
        try {
            json = mapper.writeValueAsString(room);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(roomsRepository.exists(100L)).thenReturn(true);
        try {
            mockMvc.perform(post("/room/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateRoomNoRoomType() {
        Room room = new Room(100, 1, true);
        String json = "";
        try {
            json = mapper.writeValueAsString(room);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(roomsRepository.exists(100L)).thenReturn(false);
        Mockito.when(roomsRepository.save(any(Room.class))).thenReturn(room);
        Mockito.when(roomsTypesRepository.exists(1L)).thenReturn(false);
        try {
            mockMvc.perform(post("/room/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }



}