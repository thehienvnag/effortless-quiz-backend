package com.example.springdemo.model.studentattempt;



import com.example.springdemo.model.questioninquiz.QuestionInQuiz;

import java.util.List;

public interface StudentAttemptService {
    public void save(StudentAttempt studentAttempt);
    public StudentAttempt findOne(Long id);
    public StudentAttempt findByUsersIdAndStagingQuizzesId(Long stagingQuizzesId, Integer usersId);

}
