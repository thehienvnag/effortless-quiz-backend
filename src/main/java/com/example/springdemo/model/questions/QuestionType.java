package com.example.springdemo.model.questions;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "question_type")
@Data
public class QuestionType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionType")
    private Question question;
}