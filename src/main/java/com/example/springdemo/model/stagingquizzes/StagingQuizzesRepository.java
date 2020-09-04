package com.example.springdemo.model.stagingquizzes;

import com.example.springdemo.model.questions.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StagingQuizzesRepository extends PagingAndSortingRepository<StagingQuizzes, Long> {

    @Query("SELECT q FROM StagingQuizzes q WHERE quizId = ?1 AND statusId = ?2")
    public Optional<StagingQuizzes> findByQuizIdAndStatusId(Integer quizId, Integer statusId);

    @Query("SELECT q FROM StagingQuizzes q WHERE quizId = ?1 AND statusId = ?2 AND q.quiz.usersId = ?3")
    public Optional<StagingQuizzes> findByQuizIdAndStatusIdAndUserId(Integer quizId, Integer statusId, Long usersId);

    @Query("SELECT q FROM StagingQuizzes q WHERE quizId = ?1 AND statusId = 3")
    public Optional<StagingQuizzes> findByQuizesIdAndStatusId(Integer quizesId);

    @Query("SELECT q FROM StagingQuizzes q WHERE quizId = ?1 AND statusId > 3")
    public Page<StagingQuizzes> findByQuizId(Integer quizId, Pageable pageable);

    @Query("SELECT q FROM StagingQuizzes q WHERE quizId = ?1 AND q.quiz.usersId = ?2 AND statusId > 3")
    public Page<StagingQuizzes> findByQuizIdAndUserId(Integer quizId, Long usersId, Pageable pageable);

    @Query("SELECT q FROM StagingQuizzes q WHERE q.quiz.quizCode = ?1 AND q.quiz.statusId = 1 AND statusId = 4")
    public Page<StagingQuizzes> findByQuizCodeAndStatusId(String quizCode, Pageable pageable);

    @Query("SELECT q FROM StagingQuizzes q WHERE q.id = ?1 AND q.quizPassword = ?2")
    public Optional<StagingQuizzes> findByIdAndPassword(Long id, String password);


}
