package com.example.springdemo.model.studentattempt;

import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.studentquestion.StudentQuestion;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "student_attempt")
@Entity
public class StudentAttempt implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "import_date", nullable = true)
    private Timestamp importDate;

    @Column(name = "users_id", nullable = false)
    private Integer usersId;

    @Column(name="staging_quizzes_id", nullable = true, insertable = false, updatable = false)
    private Long stagingQuizzesId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToMany(mappedBy = "studentAttempt", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<StudentQuestion> studentQuestionList;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "staging_quizzes_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private StagingQuizzes stagingQuizzes;

    @Formula("(SELECT st.description FROM staging_quizzes st WHERE st.id = staging_quizzes_id)")
    private String description;

    @Formula("(SELECT q.quiz_code FROM quizes q " +
            "WHERE q.id = (SELECT sq.quizes_id FROM staging_quizzes sq WHERE sq.id = staging_quizzes_id))")
    private String quizCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "start_time", nullable = true)
    private Timestamp startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Formula("(SELECT st.end_time FROM staging_quizzes st WHERE st.id = staging_quizzes_id)")
    private Timestamp endTime;

    @Formula("(SELECT st.duration FROM staging_quizzes st WHERE st.id = staging_quizzes_id)")
    private Integer duration;

    @Transient
    private Integer remainingTime;

    @Column(name = "status_id")
    private Integer statusId;

    public StudentAttempt() {
    }

    public StudentAttempt(Integer usersId, StagingQuizzes stagingQuizzes) {
        this.usersId = usersId;
        this.stagingQuizzes = stagingQuizzes;
        this.importDate = new Timestamp(System.currentTimeMillis());
        this.studentQuestionList = new ArrayList<>();
        this.statusId = 1;
    }

}