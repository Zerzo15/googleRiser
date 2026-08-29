package com.example.app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.app.dto.LoginRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.dto.TokenResponse;
import com.example.app.dto.UserDto;
import com.example.app.entity.User;
import com.example.app.repo.UserRepo;

@Service
public class UserService {
    private static final int MAX_USERNAME_LENGTH = 64;
    private static final int MAX_CONTACT_LENGTH = 254;
    private static final int MAX_PASSWORD_LENGTH = 72;

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserService(UserRepo userRepo, AuthenticationManager authenticationManager, JWTService jwtService) {
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserDto registerUser(RegisterRequest registerRequest) throws Exception {
        User user = new User();

        String username = registerRequest.username();
        String contact = registerRequest.contact();
        String password = registerRequest.password();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter a user name");
        }
        username = username.trim();

        if ( userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("User name is already in use");
        }
        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("User name is too long");
        }

        user.setUsername(username);

        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Provide an email or phone number");
        }

        contact = contact.trim();
        if (contact.length() > MAX_CONTACT_LENGTH) {
            throw new IllegalArgumentException("Contact is too long");
        }
        if (contact.contains("@")) {
            if (userRepo.existsByEmail(contact)) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(contact);
        } else if (contact.matches("^[0-9\\+\\-\\s]+$")) {
            if (userRepo.existsByPhone(contact)) {
                throw new IllegalArgumentException("Phone number is already in use");
            }
            user.setPhone(contact);
        } else {
            throw new IllegalArgumentException("Invalid email or phone number");
        }

        if (password == null || password.length() < 8 || password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be 8 to 72 characters");
        }

        //hash the password 12 times 
        //before saving it to the table
        user.setPassword(encoder.encode(password));

        // Public registration must never grant an administrative role.
        user.setRole("ROLE_USER");

        User savedUSer = userRepo.save(user);

        return new UserDto(savedUSer.getId(), savedUSer.getUsername());
    }

    public TokenResponse loginUser(LoginRequest loginRequest) throws Exception {
        if (loginRequest == null
                || loginRequest.loginId() == null
                || loginRequest.loginId().isBlank()
                || loginRequest.password() == null
                || loginRequest.password().isBlank()) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.loginId(), loginRequest.password())
        );

        //pull out the loginId and check inside of UserDetails 
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        String token = jwtService.generateToken(username);
        
        return new TokenResponse(token);
    }

    public UserDto getUserProfile(Long id) throws Exception {
        User user =  userRepo.findById(id).orElseThrow(() -> new Exception("User not found"));

        return new UserDto(user.getId(), user.getUsername());
    }

    public UserDto getCurrentUser(String username) throws Exception{    
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));

        return new UserDto(user.getId(), user.getUsername());
    }

    public void deleteUser(String username) throws Exception {

        if (!userRepo.existsByUsername(username)) {
            throw new RuntimeException("User Not Found");
        }
        
        userRepo.deleteByUsername(username);    
    }
}
