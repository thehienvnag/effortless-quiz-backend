package com.example.springdemo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginWithFBRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String id;

}
