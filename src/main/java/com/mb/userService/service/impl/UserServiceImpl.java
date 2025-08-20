package com.mb.userService.service.impl;

import com.mb.userService.dto.AuthResponseDto;
import com.mb.userService.dto.LoginDto;
import com.mb.userService.dto.UserDto;
import com.mb.userService.dto.UserRegistrationDto;
import com.mb.userService.exception.UserDataNotFoundException;
import com.mb.userService.model.User;
import com.mb.userService.repository.UserRepository;
import com.mb.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mb.userService.util.JwtTokenUtil;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public AuthResponseDto registerUser(UserRegistrationDto registrationDto) {
        try {
            logger.info("Starting user registration for: {}", registrationDto.getName());

            if (userRepository == null || passwordEncoder == null) {
                logger.error("Dependencies not properly initialized");
                throw new UserDataNotFoundException("Dependencies not initialized");
            }

            if (existsByName(registrationDto.getName())) {
                logger.warn("Name already exists: {}", registrationDto.getName());
                throw new UserDataNotFoundException("Name already exists");
            }

            if (existsByEmail(registrationDto.getEmail())) {
                logger.warn("Email already exists: {}", registrationDto.getEmail());
                throw new UserDataNotFoundException("Email already exists");
            }

            User user = new User();
            user.setName(registrationDto.getName());
            user.setEmail(registrationDto.getEmail());

            String rawPassword = registrationDto.getPassword();
            logger.info("Raw password length: {}", rawPassword != null ? rawPassword.length() : "null");

            String encodedPassword = passwordEncoder.encode(rawPassword);
            logger.info("Encoded password length: {}", encodedPassword != null ? encodedPassword.length() : "null");

            user.setPassword(encodedPassword);

            boolean adminExists = userRepository.existsByRole("ADMIN");
            if (!adminExists) {
                user.setRole("ADMIN");
                logger.info("First user registered as ADMIN: {}", registrationDto.getName());
            } else {
                user.setRole("AUTHOR");
                logger.info("User registered as AUTHOR: {}", registrationDto.getName());
            }

            User savedUser = userRepository.save(user);
            logger.info("User saved successfully with ID: {}", savedUser.getId());

            String token = jwtTokenUtil.generateToken(savedUser.getName());
            logger.info("JWT token generated for user: {}", savedUser.getName());

            return new AuthResponseDto(token, convertToDto(savedUser));
        } catch (Exception e) {
            logger.error("Error during user registration for {}: {}", registrationDto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AuthResponseDto loginUser(LoginDto loginDto) {
        try {
            logger.info("Starting login process for user: {}", loginDto.getName());

            if (userRepository == null || passwordEncoder == null) {
                logger.error("Dependencies not properly initialized");
                throw new RuntimeException("Dependencies not initialized");
            }

            User user = userRepository.findByName(loginDto.getName()).orElse(null);
            if (user == null) {
                logger.warn("Login failed: Name not found: {}", loginDto.getName());
                throw new RuntimeException("Invalid name or password");
            }

            logger.info("User found: {} (ID: {})",
                    user.getName(), user.getId());

            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                logger.warn("Login failed: Password mismatch for user: {}", loginDto.getName());
                throw new RuntimeException("Invalid name or password");
            }

            logger.info("Password verified successfully for user: {}", loginDto.getName());

            String token = jwtTokenUtil.generateToken(user.getName());
            logger.info("JWT token generated for user: {}", user.getName());

            return new AuthResponseDto(token, convertToDto(user));
        } catch (Exception e) {
            logger.error("Error during login for user {}: {}", loginDto.getName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDto getUserByName(String name) {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found with name: " + name));
        return convertToDto(user);
    }

    @Override
    public boolean existsByName(String name) {
        return userRepository.existsByName(name);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return convertToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(convertToDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        logger.info("Deleted user with ID: {}", id);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
