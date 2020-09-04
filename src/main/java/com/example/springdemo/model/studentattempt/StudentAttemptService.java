package com.example.springdemo.model.studentattempt;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StudentAttemptService {
    public void save(StudentAttempt studentAttempt);
    public StudentAttempt findOne(Long id);
    public StudentAttempt findByUsersIdAndStagingQuizzesId(Long stagingQuizzesId, Integer usersId);
    public Page<StudentAttempt> findByStagingQuizzesId(Long stagingQuizzesId, Pageable pageable);
    public Page<StudentAttempt> findByUsersId(Integer usersId, Pageable pageable);
    public StudentAttempt findByUserIdAndAttemptId(Long attemptId, Integer userId);
}
