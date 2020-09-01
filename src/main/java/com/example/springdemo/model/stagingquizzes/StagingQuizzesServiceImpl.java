package com.example.springdemo.model.stagingquizzes;

import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.quizes.QuizzesRepository;
import com.example.springdemo.model.quizes.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StagingQuizzesServiceImpl implements StagingQuizzesService {

    @Autowired
    private StagingQuizzesRepository stagingQuizzesRepository;

    @Override
    public void save(StagingQuizzes stagingQuizzes) {
        stagingQuizzesRepository.save(stagingQuizzes);
    }

    @Override
    public Optional<StagingQuizzes> findByQuizesIdAndStatusId(Integer quizesId, Integer statusId) {
        return stagingQuizzesRepository.findByQuizIdAndStatusId(quizesId, statusId);
    }

    @Override
    public Optional<StagingQuizzes> findOne(Long stagingQuizId) {
        return stagingQuizzesRepository.findById(stagingQuizId);
    }

    @Override
    public Page<StagingQuizzes> findByQuizId(Integer quizId, Pageable pageable) {
        return stagingQuizzesRepository.findByQuizId(quizId, pageable);
    }

    @Override
    public Page<StagingQuizzes> findByQuizCodeAndStatusId(String quizCode, Pageable pageable) {
        return stagingQuizzesRepository.findByQuizCodeAndStatusId(quizCode, pageable);
    }

    @Override
    public StagingQuizzes findByIdAndPassword(Long id, String password) {
        return stagingQuizzesRepository.findByIdAndPassword(id, password).orElse(null);
    }

}
