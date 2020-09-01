package com.example.springdemo.model.questioninquiz;

import com.example.springdemo.model.questions.Question;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.studentquestion.StudentQuestion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Table(name = "question_in_quiz")
@Entity
public class QuestionInQuiz implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;
/*
    @Column(name = "staging_quizzes_id", nullable = false)
    private Long stagingQuizzesId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;*/

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "staging_quizzes_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private StagingQuizzes stagingQuizzes;

    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionInQuiz")
    private List<StudentQuestion> studentQuestions;

    public QuestionInQuiz(){

    }

    public QuestionInQuiz(StagingQuizzes stagingQuizzes, Question question) {
        this.stagingQuizzes = stagingQuizzes;
        this.question = question;
    }


}