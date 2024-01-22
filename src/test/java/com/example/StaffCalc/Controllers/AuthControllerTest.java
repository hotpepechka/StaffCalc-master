package com.example.StaffCalc.Controllers;

import com.example.StaffCalc.controllers.AuthController;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import com.example.StaffCalc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.reflect.Array.get;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Test
    public void testList() throws Exception {
        User user = new User("John");
        user.setId(1L);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/list")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").roles("USER")))  // Add authentication
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("users", Arrays.asList(user)))
                .andExpect(MockMvcResultMatchers.view().name("users"));
    }

    @Test
    public void testAddUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/addUser").param("name", "John"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());  // Update expectation for Forbidden
    }





    @Test
    public void testEditUserForm() throws Exception {
        User user = new User("John");
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/editUser/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").roles("USER")))  // Add authentication
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("user", user))
                .andExpect(MockMvcResultMatchers.view().name("editUser"));
    }

    @Test
    public void testEditUser() throws Exception {
        User user = new User("John");
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.post("/editUser/1").param("name", "NewName")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").roles("USER")))  // Add authentication
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/deleteUser/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("username").roles("USER")))  // Add authentication
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.flash().attribute("message", "User deleted successfully"));
    }
}