package com.example.springdemo.model.responses;


import com.example.springdemo.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JwtAuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private User user;

    public JwtAuthenticationResponse(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
