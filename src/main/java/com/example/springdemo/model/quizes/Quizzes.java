package com.example.springdemo.model.quizes;

import com.example.springdemo.model.questions.Question;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.subjects.Subjects;
import com.example.springdemo.model.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "quizes")
public class Quizzes implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "users_id", nullable = false)
    private Long usersId;

    @NotBlank
    @Column(name = "quiz_code", nullable = true)
    private String quizCode;

    @NotNull
    @Column(name = "subjects_id", nullable = false)
    private Long subjectsId;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    @Column(name = "import_date", nullable = false)
    private Timestamp importDate;

    @Column(name = "status_id")
    private Integer statusId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subjects_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Subjects subjects;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<StagingQuizzes> stagingQuizzesList;

    @Formula("(" +
            "SELECT COUNT(qq.id) FROM question_in_quiz qq " +
                "WHERE " +
                    "(SELECT sq.id FROM staging_quizzes sq " +
                    "WHERE sq.status_id = 3 AND sq.quizes_id = id ) = qq.staging_quizzes_id" +
            ")")
    private Integer countQuestionInQuiz;

    @Formula("(SELECT s.name FROM status s WHERE s.id = status_id)")
    private String statusName;


}