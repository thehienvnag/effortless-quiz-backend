package com.example.springdemo.model.quizes;

import com.example.springdemo.model.questions.Question;
import com.example.springdemo.model.user.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "quizes")
public class Quizes implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "users_id", nullable = false)
    private Long usersId;

    @Column(name = "subjects_id", nullable = false)
    private Long subjectsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quiz")
    List<Question> questions;
    
}