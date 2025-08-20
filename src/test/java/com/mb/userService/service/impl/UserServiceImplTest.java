package com.mb.userService.service.impl;

import com.mb.userService.dto.AuthResponseDto;
import com.mb.userService.dto.UserDto;
import com.mb.userService.dto.UserRegistrationDto;
import com.mb.userService.exception.UserDataNotFoundException;
import com.mb.userService.model.User;
import com.mb.userService.repository.UserRepository;
import com.mb.userService.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_success() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setName("name");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setEmail("email@gmail.com");
        UserDto userDto = new UserDto(1L,userRegistrationDto.getName(),userRegistrationDto.getEmail(),
                userRegistrationDto.getPassword(),
                "Author", LocalDateTime.now(),LocalDateTime.now());
        User user = new User(userDto.getId(), userDto.getName(), userDto.getEmail(), userDto.getPassword(),
                userDto.getRole(), userDto.getCreatedAt(), userDto.getUpdatedAt());
        String encodedPassword = "qwertyuiopasdfghjkl";

        when(userRepository.existsByName(userRegistrationDto.getName())).thenReturn(false);
        when(userRepository.existsByEmail(userRegistrationDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userRegistrationDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.existsByRole("ADMIN")).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenUtil.generateToken(user.getName())).thenReturn("token");

        AuthResponseDto response = userService.registerUser(userRegistrationDto);

        assertEquals(userDto,response.getUser());
    }

    @Test
    void registerUser_NameAlreadyExists(){
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setName("name");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setEmail("email@gmail.com");

        when(userRepository.existsByName(userRegistrationDto.getName())).thenReturn(true);

        UserDataNotFoundException ex = assertThrows(
                UserDataNotFoundException.class,
                () -> userService.registerUser(userRegistrationDto)
        );

        assertEquals("Name already exists", ex.getMessage());
    }

    @Test
    void registerUser_EmailAlreadyExists(){
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setName("name");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setEmail("email@gmail.com");

        when(userRepository.existsByEmail(userRegistrationDto.getEmail())).thenReturn(true);

        UserDataNotFoundException ex = assertThrows(
                UserDataNotFoundException.class,
                () -> userService.registerUser(userRegistrationDto)
        );

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void loginUser() {
    }

    @Test
    void getUserByName() {
    }

    @Test
    void existsByName() {
    }

    @Test
    void existsByEmail() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}