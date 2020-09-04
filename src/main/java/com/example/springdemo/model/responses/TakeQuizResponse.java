package com.example.springdemo.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class TakeQuizResponse {
    private Long id;
    private String message;
}
