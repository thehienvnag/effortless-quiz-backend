package com.example.springdemo.model.quizes;

import com.example.springdemo.model.questions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizesServiceImpl implements QuizesService {

    @Autowired
    private QuizesRepository quizesRepository;

    @Override
    public void save(Quizes quizes) {
        quizesRepository.save(quizes);
    }




}
