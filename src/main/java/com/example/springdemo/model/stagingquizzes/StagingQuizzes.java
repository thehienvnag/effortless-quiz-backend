package com.example.springdemo.model.stagingquizzes;

import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.studentattempt.StudentAttempt;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staging_quizzes")
public class StagingQuizzes implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "start_time", nullable = true)
    private Timestamp startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "end_time", nullable = true)
    private Timestamp endTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "import_date", nullable = true)
    private Timestamp importDate;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "duration", nullable = true)
    private Integer duration;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "quiz_password", nullable = false)
    private String quizPassword;

    @Formula("(SELECT s.name FROM status s WHERE s.id = status_id)")
    private String statusName;

    @Column(name="quizes_id", insertable = false, updatable = false)
    private Integer quizId;

    @Column(name = "reviewable", nullable = true)
    private Boolean reviewable;

    @Formula("(SELECT q.quiz_code FROM quizes q WHERE q.id = quizes_id)")
    private String quizCode;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizes_id", referencedColumnName = "id")
    private Quizzes quiz;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "stagingQuizzes", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuestionInQuiz> questionInQuizList;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "stagingQuizzes", fetch = FetchType.LAZY)
    private List<StudentAttempt> studentAttemptList;

    public StagingQuizzes(Integer statusId, Quizzes quiz) {
        this.statusId = statusId;
        this.quiz = quiz;
        this.questionInQuizList = new ArrayList<>();
    }
}