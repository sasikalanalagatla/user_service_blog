package com.mb.userService.controller;

import com.mb.userService.dto.AuthResponseDto;
import com.mb.userService.dto.LoginDto;
import com.mb.userService.dto.UserDto;
import com.mb.userService.dto.UserRegistrationDto;
import com.mb.userService.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    @Test
    void registerUser_success() {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setName("TestUser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");

        UserDto userDto = new UserDto(1L, "TestUser", "test@example.com", "encodedPass",
                "AUTHOR", LocalDateTime.now(), LocalDateTime.now());
        AuthResponseDto authResponseDto = new AuthResponseDto("mockToken123", userDto);

        when(userService.registerUser(registrationDto)).thenReturn(authResponseDto);

        ResponseEntity<AuthResponseDto> response = userController.registerUser(registrationDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("mockToken123", response.getBody().getToken());

        verify(userService, times(1)).registerUser(registrationDto);
    }

    @Test
    void registerUser_failure(){
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setName("TestUser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");

        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Name already exists"));

        ResponseEntity<AuthResponseDto> response = userController.registerUser(registrationDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        verify(userService, times(1)).registerUser(registrationDto);
    }

    @Test
    void loginUser_success() {
        LoginDto login = new LoginDto("name","password");
        UserDto userDto = new UserDto(1L, "TestUser", "test@example.com", "encodedPass",
                "AUTHOR", LocalDateTime.now(), LocalDateTime.now());
        AuthResponseDto authResponseDto = new AuthResponseDto("mockToken123", userDto);

        when(userService.loginUser(login)).thenReturn(authResponseDto);

        ResponseEntity<AuthResponseDto> response = userController.loginUser(login);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userService, times(1)).loginUser(login);
    }

    @Test
    void loginUser_failure() {
        LoginDto login = new LoginDto("wrongName", "wrongPassword");

        when(userService.loginUser(login)).thenThrow(new RuntimeException("Invalid name or password"));

        ResponseEntity<AuthResponseDto> response = userController.loginUser(login);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Login failed: Invalid name or password", response.getBody().getToken());
        assertNull(response.getBody().getUser());

        verify(userService, times(1)).loginUser(login);
    }

    @Test
    void getUserById() {
        Long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setRole("Admin");
        user.setName("name");
        user.setEmail("email@gmail.com");
        user.setPassword("password");
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Admin", response.getBody().getRole());

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserByName() {
        String name = "name";
        UserDto user = new UserDto();
        user.setId(1L);
        user.setRole("Admin");
        user.setName(name);
        user.setEmail("email@gmail.com");
        user.setPassword("password");
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        when(userService.getUserByName(name)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.getUserByName(name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(name, response.getBody().getName());

        verify(userService, times(1)).getUserByName(name);
    }

    @Test
    void getAllUsers() {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setRole("Admin");
        user.setName("name");
        user.setEmail("email@gmail.com");
        user.setPassword("password");
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        List<UserDto> users = List.of(user);

        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser() {
        Long userId = 1L;
        UserDto user = new UserDto();
        user.setId(userId);
        user.setRole("Admin");
        user.setName("name");
        user.setEmail("email@gmail.com");
        user.setPassword("password");
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        when(userService.updateUser(userId,user)).thenReturn(user);

        ResponseEntity<UserDto> response = userController.updateUser(userId, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userService, times(1)).updateUser(userId, user);
    }

    @Test
    void deleteUser() {
        Long userId = 1L;

        userService.deleteUser(userId);

        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(userService, times(2)).deleteUser(userId);
    }

    @Test
    void checkNameExists() {
        String name = "name";

        when(userService.existsByName(name)).thenReturn(true);

        ResponseEntity<Boolean> response = userController.checkNameExists(name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());

        verify(userService, times(1)).existsByName(name);
    }

    @Test
    void checkEmailExists() {
        String email = "email@gmail.com";

        when(userService.existsByEmail(email)).thenReturn(true);

        ResponseEntity<Boolean> response = userController.checkEmailExists(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());

        verify(userService, times(1)).existsByEmail(email);
    }

    @Test
    void logoutUser_success() {
        String token = "mockToken123";

        ResponseEntity<String> response = userController.logoutUser(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful", response.getBody());
    }
}