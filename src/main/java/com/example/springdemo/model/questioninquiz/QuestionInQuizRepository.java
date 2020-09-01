package com.example.springdemo.model.questioninquiz;

import com.example.springdemo.model.questions.Question;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionInQuizRepository extends PagingAndSortingRepository<QuestionInQuiz, Long> {

}
