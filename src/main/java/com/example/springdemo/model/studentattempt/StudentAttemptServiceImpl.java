package com.example.springdemo.model.studentattempt;

import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentAttemptServiceImpl implements StudentAttemptService {

    @Autowired
    private StudentAttemptRepository studentAttemptRepository;


    @Override
    public void save(StudentAttempt studentAttempt) {
        studentAttemptRepository.save(studentAttempt);
    }

    @Override
    public StudentAttempt findOne(Long id) {
        return studentAttemptRepository.findById(id).orElse(null);
    }

    @Override
    public StudentAttempt findByUsersIdAndStagingQuizzesId(Long stagingQuizzesId, Integer usersId) {
        return studentAttemptRepository.findByUsersIdAndStagingQuizzesId(stagingQuizzesId, usersId).orElse(null);
    }
}
