package com.example.springdemo.model.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter@Setter
public class GiveAnswerRequest {
    @NotBlank
    @Pattern(regexp = "([,]*\\d+[,]*)+")
    private String chosenAnswerId;

}
