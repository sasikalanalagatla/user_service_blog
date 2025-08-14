package com.mb.userService.service;

import com.mb.userService.dto.AuthResponseDto;
import com.mb.userService.dto.LoginDto;
import com.mb.userService.dto.UserDto;
import com.mb.userService.dto.UserRegistrationDto;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    
    AuthResponseDto registerUser(UserRegistrationDto registrationDto);
    
    AuthResponseDto loginUser(LoginDto loginDto);

    UserDto getUserByName(String name);

    boolean existsByName(String name);
    
    boolean existsByEmail(String email);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(Long id, @Valid UserDto userDto);

    void deleteUser(Long id);
}
