package com.example.springdemo.model.responses;


import com.example.springdemo.security.UserPrincipal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JwtAuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private UserPrincipal user;

    public JwtAuthenticationResponse(String accessToken, String refreshToken, UserPrincipal user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
