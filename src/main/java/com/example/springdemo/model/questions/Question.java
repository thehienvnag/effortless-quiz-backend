package com.example.springdemo.model.questions;

import com.example.springdemo.model.answer.Answer;
import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import com.example.springdemo.model.quizes.Quizzes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "question")
@Data
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "code_content")
    private String codeContent;

    @Column(name = "question_type_id", nullable = false)
    private Integer questionTypeId;

    @Column(name = "quiz_pos", nullable = false)
    private Integer quizPos;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Formula("(SELECT COUNT(a.id) FROM answer a WHERE a.question_id = id AND a.correct = true)")
    private Integer countCorrectAnswer;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QuestionType questionType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "question")
    private List<Answer> answerList;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionInQuiz> questionInQuiz;

}