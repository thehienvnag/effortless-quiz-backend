package com.example.springdemo.controllers;

import com.example.springdemo.model.requests.LoginRequest;
import com.example.springdemo.model.responses.JwtAuthenticationResponse;
import com.example.springdemo.model.role.Role;
import com.example.springdemo.model.user.User;
import com.example.springdemo.model.user.UserServiceImpl;
import com.example.springdemo.model.userrole.UserRole;
import com.example.springdemo.security.JwtTokenProvider;
import com.example.springdemo.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8081")
//@CrossOrigin(origins = "https://effortless-quiz.herokuapp.com")
public class UserController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @GetMapping("/users/{id}")
    public ResponseEntity getUserInfo(@PathVariable Integer id) {
        User user = userService.findUser(id).orElse(null);
        if(user == null){
            return new ResponseEntity("User does not exist", HttpStatus.NO_CONTENT);
        }
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId().toString());
        user.setRefreshToken(refreshToken);
        userService.saveUser(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken,refreshToken,user));
    }


}
