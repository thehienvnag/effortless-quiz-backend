package com.example.springdemo.model.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class TakeQuizRequest {
    @NotNull
    private Long stagingQuizzesId;
    @NotNull
    private String quizPassword;
}
