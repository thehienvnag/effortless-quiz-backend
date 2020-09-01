package com.example.springdemo.model.quizes;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QuizzesService {
    public void save(Quizzes quizzes);
    public Page<Quizzes> findByUsersId(Long userId, Pageable pageable);
    public Page<Quizzes> findByQuizCodeContaining(String q, Pageable pageable);
    public Page<Quizzes> findByQuizCodeAndUsersId(String q, Pageable pageable, Long userId);
    public Optional<Quizzes> findByCodeAndStatus(String quizCode, Integer statusId);
    public Optional<Quizzes> findByIdAndUsersId(Long userId, Integer quizId);
    public Optional<Quizzes> findById(Integer id);
    public Optional<Quizzes> findByQuizCode(String quizCode);

}
