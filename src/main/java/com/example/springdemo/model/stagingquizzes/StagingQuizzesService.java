package com.example.springdemo.model.stagingquizzes;



import com.example.springdemo.model.questions.Question;
import com.example.springdemo.model.quizes.Quizzes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StagingQuizzesService {
    public void save(StagingQuizzes question);
    public Optional<StagingQuizzes> findByQuizesIdAndStatusId(Integer quizesId, Integer statusId);
    public Optional<StagingQuizzes> findOne(Long stagingQuizId);
    public Page<StagingQuizzes> findByQuizId(Integer quizId, Pageable pageable);
    public Page<StagingQuizzes> findByQuizCodeAndStatusId(String quizCode, Pageable pageable);

    public StagingQuizzes findByIdAndPassword(Long id, String password);
}
