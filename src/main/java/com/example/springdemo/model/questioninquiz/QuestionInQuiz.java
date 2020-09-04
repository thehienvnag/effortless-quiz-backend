package com.example.springdemo.model.questioninquiz;

import com.example.springdemo.model.questions.Question;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.studentquestion.StudentQuestion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Formula;

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

    @Formula("(SELECT COUNT(sq.id) FROM student_question sq WHERE sq.question_in_quiz_id = id)")
    private Integer countStudentQuestionJoined;

    @Formula("(SELECT COUNT(qq.id) FROM question_in_quiz qq " +
            "WHERE qq.question_id = question_id AND qq.staging_quizzes_id <> staging_quizzes_id)")
    private Integer countQuestionInAnotherQuiz;

    @Column(name = "status_id")
    private Integer statusId;

    public QuestionInQuiz(){

    }

    public QuestionInQuiz(StagingQuizzes stagingQuizzes, Question question) {
        this.stagingQuizzes = stagingQuizzes;
        this.question = question;
        this.statusId = 1;
    }


}