package com.example.springdemo.model.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter @Setter
public class RefreshRequest implements Serializable {
    @NotBlank
    private String refreshToken;

}
