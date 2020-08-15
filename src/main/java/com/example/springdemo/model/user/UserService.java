package com.example.springdemo.model.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    public UserDetails findOne(Integer id);
    public Optional<User> findUser(Integer id);
    public void saveUser(User user);
}
