package com.example.springdemo.model.quizes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizzesRepository extends PagingAndSortingRepository<Quizzes, Integer> {

    public Page<Quizzes> findByUsersId(Long usersId, Pageable pageable);

    public Page<Quizzes> findByQuizCodeContaining(String q, Pageable pageable);

    @Query("SELECT q FROM Quizzes q WHERE quizCode LIKE ?1 AND usersId = ?2")
    public Page<Quizzes> findByQuizCodeAndUsersId(String q, Long usersId, Pageable pageable);

    @Query("SELECT q FROM Quizzes q WHERE quizCode = ?1 AND statusId = ?2")
    public Optional<Quizzes> findByCodeAndStatus(String quizCode, Integer statusId);

    @Query("SELECT q FROM Quizzes q WHERE usersId = ?1 AND id = ?2")
    public Optional<Quizzes> findByIdAndUsersId(Long usersId, Integer quizId);

    public Optional<Quizzes> findById(Integer id);

    public Optional<Quizzes> findByQuizCode(String quizCode);

}
