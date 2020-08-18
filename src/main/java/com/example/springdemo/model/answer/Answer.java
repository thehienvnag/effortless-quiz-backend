//package com.example.springdemo.model.answer;
//
//import com.example.springdemo.model.questions.Question;
//import lombok.Data;
//
//import javax.persistence.*;
//import java.io.Serializable;
//
//@Entity
//@Data
//@Table(name = "answer")
//public class Answer implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", insertable = false, nullable = false)
//    private Long id;
//
//    @Column(name = "content", nullable = false)
//    private String content;
//
//    @Column(name = "correct", nullable = false)
//    private Boolean correct;
//
//    @Column(name = "question_id", nullable = false)
//    private String questionId;
//
//    @Column(name = "status_id", nullable = false)
//    private Integer statusId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "question_id", referencedColumnName = "id")
//    private Question question;
//
//
//}