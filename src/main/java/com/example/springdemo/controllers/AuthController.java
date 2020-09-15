package com.example.springdemo.controllers;

import com.example.springdemo.model.requests.*;
import com.example.springdemo.model.responses.JwtAuthenticationResponse;
import com.example.springdemo.model.role.Role;
import com.example.springdemo.model.user.User;
import com.example.springdemo.model.user.UserServiceImpl;
import com.example.springdemo.model.userrole.UserRole;
import com.example.springdemo.model.userrole.UserRoleService;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081")
//@CrossOrigin(origins = "https://effortless-quiz.herokuapp.com")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRoleService userRoleService;

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


        Integer userId = ((UserPrincipal) auth.getPrincipal()).getId();
        User userFound = userService.findUser(userId).orElse(null);
        if (userFound == null) {
            return new ResponseEntity("Incorrect username or password!!", HttpStatus.UNAUTHORIZED);
        }
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);
        userFound.setRefreshToken(refreshToken);
        userService.saveUser(userFound);
        return ResponseEntity.ok(
                new JwtAuthenticationResponse(accessToken, refreshToken, userFound)
        );
    }

    @PostMapping("/login-with-facebook")
    public ResponseEntity loginWithFacebook(@Valid @RequestBody LoginWithFBRequest loginRequest) {
        String fbID = loginRequest.getId();
        String name = loginRequest.getName();

        User user = userService.findUserByFacebookId(fbID).orElse(null);
        if (user == null) {
            user = new User(name, fbID);
            userService.saveUser(user);
        }


        if (user.getUserRoles() != null && user.getUserRoles().size() > 0) {
            userService.saveUser(user);
            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user.getId().toString());
            return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken, user));
        }
        return ResponseEntity.ok(new JwtAuthenticationResponse(null, null, user));
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshToken(@RequestBody RefreshRequest refreshRequest) {
        Integer userId = tokenProvider.getUserIdFromJWT(refreshRequest.getRefreshToken());

        User userFound = userService.findUser(userId).orElse(null);

        if (userFound == null) {
            return ResponseEntity.badRequest().body("Invalid Request!!");
        }
        if (userFound.getRefreshToken() != null && userFound.getRefreshToken().equals(refreshRequest.getRefreshToken())) {
            String accessToken = tokenProvider.generateAccessToken(userFound);
            String refreshToken = tokenProvider.generateRefreshToken(userId.toString());
            userFound.setRefreshToken(refreshToken);
            userService.saveUser(userFound);
            userFound.setPassword(null);

            return ResponseEntity.ok(
                    new JwtAuthenticationResponse(accessToken, refreshToken, userFound)
            );
        }
        return new ResponseEntity("Try login again!", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest registerRequest) {

        String username = registerRequest.getUsername();
        String password = passwordEncoder.encode(
                registerRequest.getPassword()
        );
        String name = registerRequest.getName();
        String roleId = registerRequest.getRoleId();
        User user = new User(username, password, name, roleId);
        try {
            userService.saveUser(user);
        } catch (Exception e) {
            String msg = e.getMessage();
            logger.error(msg);
            if (msg.contains("unique_user_Username")) {
                return ResponseEntity.badRequest().body("Username already exists!");
            }
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity updateRoleUser(@PathVariable Integer id, @RequestBody UpdateRoleRequest updateRequest) {

        User user = userService.findUser(id).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User id specified does not exist!");
        }
        if(user.getUserRoles().size() > 0){
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        List<UserRole> userRoles = new ArrayList<>();
        Role role = new Role(Integer.parseInt(updateRequest.getRoleId()));
        UserRole userRole = new UserRole(user, role);
        String roleName = role.getName();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getName(), roleName);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId().toString());
        user.setRefreshToken(refreshToken);
        userService.saveUser(user);
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken, user));
    }
}
