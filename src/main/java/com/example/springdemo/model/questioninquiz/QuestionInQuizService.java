package com.example.springdemo.model.questioninquiz;



import com.example.springdemo.model.questions.Question;

import java.util.List;

public interface QuestionInQuizService {
    public void saveAll(List<QuestionInQuiz> questions);
    public void deleteCollections(List<QuestionInQuiz> questionInQuizList);
    public QuestionInQuiz findOne(Long id);
}
