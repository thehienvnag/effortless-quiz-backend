package com.example.springdemo.model.responses;


import com.example.springdemo.security.UserPrincipal;

public class JwtAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private UserPrincipal user;

    public JwtAuthenticationResponse(String accessToken, UserPrincipal user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(UserPrincipal user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
