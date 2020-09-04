package com.example.springdemo.model.userrole;

import com.example.springdemo.model.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserRoleService{
    public void removeRole(UserRole userRole);
}
