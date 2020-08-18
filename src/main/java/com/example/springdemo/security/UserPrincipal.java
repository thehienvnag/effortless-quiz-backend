package com.example.springdemo.security;

import com.example.springdemo.model.student.Student;
import com.example.springdemo.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private Integer id;

    private String username;

    private String name;

    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    private String password;

    public UserPrincipal(Integer id, String username, String name, String password, String facebookId, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getUserRoles()
                .stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                .collect(Collectors.toList());
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getPassword(),
                user.getFacebookId(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
