package com.example.app.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.dto.TokenResponse;
import com.example.app.dto.UserDto;
import com.example.app.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) throws Exception {
        UserDto savedUser = userService.registerUser(registerRequest);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) throws Exception {
        TokenResponse successMessage = userService.loginUser(loginRequest);
        return ResponseEntity.ok(successMessage);
    }   

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) throws Exception {
        UserDto user = userService.getUserProfile(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) throws Exception {
        UserDto user = userService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username) throws Exception {
        userService.deleteUser(username);
        return ResponseEntity.ok("Account Delete Successful");
    }
}