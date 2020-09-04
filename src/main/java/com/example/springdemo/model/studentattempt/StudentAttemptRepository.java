package com.example.springdemo.model.studentattempt;

import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentAttemptRepository extends PagingAndSortingRepository<StudentAttempt, Long> {
    @Query("SELECT sa FROM StudentAttempt sa WHERE sa.stagingQuizzesId = ?1 AND sa.usersId = ?2")
    public Optional<StudentAttempt> findByUsersIdAndStagingQuizzesId(Long stagingQuizzesId, Integer usersId);

    @Query("SELECT sa FROM StudentAttempt sa WHERE sa.usersId = ?1 AND sa.statusId = 2")
    public Page<StudentAttempt> findByUsersIdAndStatusId(Integer usersId, Pageable pageable);

    @Query("SELECT sa FROM StudentAttempt sa WHERE sa.id = ?1 AND sa.usersId = ?2")
    public Optional<StudentAttempt> findByUsersIdAndId(Long attemptId, Integer usersId);

    @Query("SELECT sa FROM StudentAttempt sa WHERE sa.stagingQuizzesId = ?1")
    public Page<StudentAttempt> findByStagingQuizzesId(Long stagingQuizzesId, Pageable pageable);
}
