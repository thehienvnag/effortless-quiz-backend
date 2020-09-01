package com.example.springdemo.model.questioninquiz;

import com.example.springdemo.model.questions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionInQuizServiceImpl implements QuestionInQuizService {

    @Autowired
    private QuestionInQuizRepository questionInQuizRepository;


    @Override
    public void saveAll(List<QuestionInQuiz> questions) {
        for (QuestionInQuiz questionInQuiz : questions) {
            questionInQuizRepository.save(questionInQuiz);
        }
    }

    @Override
    public void deleteCollections(List<QuestionInQuiz> questionInQuizList) {
        for (QuestionInQuiz questionInQuiz : questionInQuizList) {
            questionInQuizRepository.deleteById(questionInQuiz.getId());
        }
    }

    @Override
    public QuestionInQuiz findOne(Long id) {
        return questionInQuizRepository.findById(id).orElse(null);
    }
}
