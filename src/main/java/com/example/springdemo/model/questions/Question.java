package com.example.springdemo.model.questions;

import com.example.springdemo.model.answer.Answer;
import com.example.springdemo.model.quizes.Quizes;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "question")
@Data
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Id
    @Column(name = "content", insertable = false, nullable = false)
    private String content;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "code_content")
    private String codeContent;

    @Column(name = "code_type")
    private String codeType;

    @Column(name = "quizes_id", nullable = false)
    private Integer quizesId;

    @Column(name = "question_type_id", nullable = false)
    private Integer questionTypeId;

    @Column(name = "quiz_pos", nullable = false)
    private Integer quizPos;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizes_id", referencedColumnName = "id")
    private Quizes quiz;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "question_type_id", referencedColumnName = "id")
//    private QuestionType questionType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "question")
    private List<Answer> answerList;
    
}