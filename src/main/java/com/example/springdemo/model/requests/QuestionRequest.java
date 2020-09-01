package com.example.springdemo.model.requests;

import com.example.springdemo.model.questions.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class QuestionRequest implements Serializable {
    private List<Question> questions;
}
