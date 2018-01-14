package com.users.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.models.User;
import com.users.repos.UserRepository;
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
public class UserControllerTest {

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void shouldFindUserById() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        Mockito.when(userRepository.exists(1L)).thenReturn(true);
        Mockito.when(userRepository.findOne(1L)).thenReturn(user);
        try {
            mockMvc.perform(get("/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.firstName", is("Janna")))
                    .andExpect(jsonPath("$.lastName", is("Shapoval")))
                    .andExpect(jsonPath("$.ordersAmount", is(0)));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindUserById() {
        Mockito.when(userRepository.exists(1L)).thenReturn(false);
        try {
            mockMvc.perform(get("/user/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindAllUsers() {
        List<User> users = new LinkedList<>();
        users.add(new User(1L,12345678L, "Janna", "Shapoval", 0));
        users.add(new User(2L,11111111L, "Masha", "Kasha", 3));
        Integer page = 0;
        Integer size = 2;
        Mockito.when(userRepository.findAll(new PageRequest(page, size))).thenReturn(users);
        try {
            mockMvc.perform(get(String.format("/user/users?page=%d&size=%d", page, size)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals("bad", users, userController.findAll(page, size).getBody());
    }

    @Test
    public void shouldNotFindAllUsersBadPage() {
        Integer page = -1;
        Integer size = 2;
        try {
            mockMvc.perform(get(String.format("/user/users?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindAllUsersBadSize() {
        Integer page = 1;
        Integer size = -2;
        try {
            mockMvc.perform(get(String.format("/user/users?page=%d&size=%d", page, size)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldFindUserByPassport() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        Mockito.when(userRepository.findByPassportNumber(user.getPassportNumber())).thenReturn(user);
        try {
            mockMvc.perform(get(String.format("/user/passport/%d", user.getPassportNumber())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.firstName", is("Janna")))
                    .andExpect(jsonPath("$.lastName", is("Shapoval")))
                    .andExpect(jsonPath("$.ordersAmount", is(0)));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotFindUserByPassport() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        Mockito.when(userRepository.findByPassportNumber(user.getPassportNumber())).thenReturn(null);
        try {
            mockMvc.perform(get(String.format("/user/passport/%d", user.getPassportNumber())))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteUserById() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        Mockito.when(userRepository.exists(1L)).thenReturn(true);
        Mockito.doNothing().when(userRepository).delete(1L);
        try {
            mockMvc.perform(delete(String.format("/user/delete/%d", user.getId())))
                    .andExpect(status().isOk());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotDeleteUserById() {
        long userId = 1L;
        Mockito.when(userRepository.exists(userId)).thenReturn(false);
        try {
            mockMvc.perform(delete(String.format("/user/delete/%d", userId)))
                    .andExpect(status().isNotFound());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldCreateRoom() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        String json = "";
        try {
            json = mapper.writeValueAsString(user);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(userRepository.findByPassportNumber(12345678L)).thenReturn(null);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        try {
            mockMvc.perform(post("/user/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.firstName", is("Janna")))
                    .andExpect(jsonPath("$.lastName", is("Shapoval")))
                    .andExpect(jsonPath("$.ordersAmount", is(0)))
                    .andExpect(header().string("location", "http://localhost/user/1"));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateRoomConflict() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        String json = "";
        try {
            json = mapper.writeValueAsString(user);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(userRepository.findByPassportNumber(12345678L)).thenReturn(user);
        try {
            mockMvc.perform(post("/user/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void shouldNotCreateRoomServerError() {
        User user = new User(1L,12345678L, "Janna", "Shapoval", 0);
        String json = "";
        try {
            json = mapper.writeValueAsString(user);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        Mockito.when(userRepository.findByPassportNumber(12345678L)).thenReturn(null);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(null);

        try {
            mockMvc.perform(post("/user/create")
                    .content(json)
                    .header("Content-Type",  "application/json"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}