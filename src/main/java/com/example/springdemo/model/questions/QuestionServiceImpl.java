package com.example.springdemo.model.questions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void saveAll(List<Question> questions) {
        questionRepository.saveAll(questions);
    }

    @Override
    public void delete(Question question) {
        questionRepository.delete(question);
    }


}
