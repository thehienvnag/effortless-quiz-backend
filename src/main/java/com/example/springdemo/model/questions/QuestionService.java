package com.example.springdemo.model.questions;



import java.util.List;
import java.util.Optional;

public interface QuestionService {
    public void saveAll(List<Question> questions);
    public void delete(Question question);

}
