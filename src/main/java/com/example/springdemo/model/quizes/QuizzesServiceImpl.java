package com.example.springdemo.model.quizes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    @Autowired
    private QuizzesRepository quizzesRepository;

    @Override
    public void save(Quizzes quizzes) {
        quizzesRepository.save(quizzes);
    }

    @Override
    public Page<Quizzes> findByUsersId(Long userId, Pageable pageable) {
        return quizzesRepository.findByUsersId(userId, pageable);
    }

    @Override
    public Page<Quizzes> findByQuizCodeContaining(String q, Pageable pageable) {
        return quizzesRepository.findByQuizCodeContaining(q, pageable);
    }

    @Override
    public Page<Quizzes> findByQuizCodeAndUsersId(String q, Pageable pageable, Long userId) {
        return quizzesRepository.findByQuizCodeAndUsersId(q, userId, pageable);
    }

    @Override
    public Optional<Quizzes> findByCodeAndStatus(String quizCode, Integer statusId) {
        return quizzesRepository.findByCodeAndStatus(quizCode, statusId);
    }

    @Override
    public Optional<Quizzes> findByIdAndUsersId(Long userId, Integer quizId) {
        return quizzesRepository.findByIdAndUsersId(userId, quizId);
    }

    public Optional<Quizzes> findById(Integer id){
        return quizzesRepository.findById(id);
    }

    @Override
    public Optional<Quizzes> findByQuizCode(String quizCode) {
        return quizzesRepository.findByQuizCode(quizCode);
    }


}
