package com.example.springdemo.model.studentattempt;

import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentAttemptRepository extends PagingAndSortingRepository<StudentAttempt, Long> {
    @Query("SELECT sa FROM StudentAttempt sa WHERE sa.stagingQuizzesId = ?1 AND sa.usersId = ?2")
    public Optional<StudentAttempt> findByUsersIdAndStagingQuizzesId(Long stagingQuizzesId, Integer usersId);
}
