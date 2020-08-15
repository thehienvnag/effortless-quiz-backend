package com.example.springdemo.controllers;

import com.example.springdemo.model.requests.LoginRequest;
import com.example.springdemo.model.requests.RegisterRequest;
import com.example.springdemo.model.responses.JwtAuthenticationResponse;
import com.example.springdemo.model.user.User;
import com.example.springdemo.model.user.UserServiceImpl;
import com.example.springdemo.security.JwtTokenProvider;
import com.example.springdemo.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity signIn(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        //SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(auth);

        return ResponseEntity.ok(
                new JwtAuthenticationResponse(token, (UserPrincipal) auth.getPrincipal())
        );
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest registerRequest) {

        String username = registerRequest.getUsername();
        String password = passwordEncoder.encode(
                registerRequest.getPassword()
        );
        String name = registerRequest.getName();
        User user = new User(username, password, name);
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            String msg = e.getMessage();
            logger.error(msg);
            if (msg.contains("user.Username_UNIQUE")) {
                return ResponseEntity.ok("Username already exists!");
            }
        }
        return ResponseEntity.ok(user);
    }
}
