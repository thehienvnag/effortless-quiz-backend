package com.example.springdemo.model.studentquestion;

import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import com.example.springdemo.model.studentattempt.StudentAttempt;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Entity
@Table(name = "student_question")
public class StudentQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @Column(name = "chosen_answer_id")
    private String chosenAnswerId;

    @Column(name = "student_attempt_id", insertable = false, updatable = false)
    private Long studentAttemptId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "student_attempt_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private StudentAttempt studentAttempt;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_in_quiz_id", referencedColumnName = "id")
    private QuestionInQuiz questionInQuiz;

    @Formula("(SELECT sq.status_id FROM staging_quizzes sq " +
            "WHERE sq.id = (SELECT sa.staging_quizzes_id FROM student_attempt sa WHERE sa.id = student_attempt_id)" +
            ")")
    private Integer stagingQuizzesStatusId;

    @Formula("(SELECT sa.status_id FROM student_attempt sa " +
            "WHERE sa.id = student_attempt_id" +
            ")")
    private Integer studentAttemptStatusId;

    @Formula("(SELECT sa.start_time FROM student_attempt sa " +
            "WHERE sa.id = student_attempt_id" +
            ")")
    private Timestamp startTime;

    @Formula("(SELECT st.duration FROM staging_quizzes st " +
            "WHERE st.id = (SELECT sa.staging_quizzes_id FROM student_attempt sa WHERE sa.id = student_attempt_id))")
    private Integer duration;

    public StudentQuestion(StudentAttempt studentAttempt, QuestionInQuiz questionInQuiz) {
        this.studentAttempt = studentAttempt;
        this.questionInQuiz = questionInQuiz;
    }


}