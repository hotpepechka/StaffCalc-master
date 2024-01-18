package com.example.StaffCalc.Controllers;

import com.example.StaffCalc.controllers.DataController;
import com.example.StaffCalc.models.User;
import com.example.StaffCalc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class DataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DataController dataController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(dataController).build();

        // Очистите репозиторий перед каждым тестом
        reset(userRepository);

        // Создайте и сохраните пользователя с именем "AnyName" в репозитории во время настройки теста
        User user = new User("AnyName");
        user.setId(1L); // Установка идентификатора
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));


    }


    @Test
    public void testAddUser() throws Exception {
        mockMvc.perform(post("/addUser").param("name", "Ilya"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));

        // Проверьте, что метод save был вызван с ожидаемым пользователем
        verify(userRepository, times(1)).save(argThat(user -> "Ilya".equals(user.getName())));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testList() throws Exception {
        // Предположим, что в репозитории есть пользователь с именем "AnyName"
        System.out.println("Users from repository: " + userRepository.findAll());  // Добавьте эту строку
        mockMvc.perform(get("/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("users"))
                .andExpect(content().string(containsString("AnyName")));

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    public void testEditUserForm() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User("Ilya")));

        mockMvc.perform(get("/editUser/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("editUser"))
                .andExpect(content().string(containsString("Ilya")));
        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testEditUser() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/editUser/1").param("name", "UpdatedName"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(get("/deleteUser/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));

        verify(userRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}
