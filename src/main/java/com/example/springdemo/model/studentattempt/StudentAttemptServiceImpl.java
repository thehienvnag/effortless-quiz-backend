package com.example.springdemo.model.studentattempt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    @Override
    public Page<StudentAttempt> findByStagingQuizzesId(Long stagingQuizzesId, Pageable pageable) {
        return studentAttemptRepository.findByStagingQuizzesId(stagingQuizzesId, pageable);
    }

    @Override
    public Page<StudentAttempt> findByUsersId(Integer usersId, Pageable pageable) {
        return studentAttemptRepository.findByUsersIdAndStatusId(usersId, pageable);
    }

    @Override
    public StudentAttempt findByUserIdAndAttemptId(Long attemptId, Integer userId) {
        return studentAttemptRepository.findByUsersIdAndId(attemptId, userId).orElse(null);
    }

}
