package com.example.springdemo.model.questions;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends PagingAndSortingRepository<Question, Integer> {

    public Optional<Question> findByContentContaining(String content);




}
