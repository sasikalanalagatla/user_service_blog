package com.mb.userService.controller;

import com.mb.userService.dto.AuthResponseDto;
import com.mb.userService.dto.LoginDto;
import com.mb.userService.dto.UserDto;
import com.mb.userService.dto.UserRegistrationDto;
import com.mb.userService.service.UserService;
import com.mb.userService.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        logger.info("=== REGISTRATION REQUEST RECEIVED ===");

        try {
            AuthResponseDto response = userService.registerUser(registrationDto);
            logger.info("User registration successful for: {}", registrationDto.getName());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("User registration failed for: {}, error: {}", registrationDto.getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponseDto("Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
        logger.info("=== LOGIN REQUEST RECEIVED ===");

        try {
            AuthResponseDto response = userService.loginUser(loginDto);
            logger.info("User login successful for: {}", loginDto.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("User login failed for: {}, error: {}", loginDto.getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponseDto("Login failed: " + e.getMessage(), null));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/name/{name}")
    public ResponseEntity<UserDto> getUserByName(@PathVariable String name) {
        UserDto user = userService.getUserByName(name);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/check-name/{name}")
    public ResponseEntity<Boolean> checkNameExists(@PathVariable String name) {
        boolean exists = userService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/users/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logoutUser(@RequestParam String token) {
        try {
            logger.info("Logout request received for token");
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Logout failed: " + e.getMessage());
        }
    }
}
