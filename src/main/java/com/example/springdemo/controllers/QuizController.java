package com.example.springdemo.controllers;

import com.example.springdemo.model.answer.Answer;
import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import com.example.springdemo.model.questioninquiz.QuestionInQuizService;
import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.quizes.QuizzesService;
import com.example.springdemo.model.requests.GiveAnswerRequest;
import com.example.springdemo.model.requests.TakeQuizRequest;
import com.example.springdemo.model.responses.BadRequestResponse;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.stagingquizzes.StagingQuizzesService;
import com.example.springdemo.model.studentattempt.StudentAttempt;
import com.example.springdemo.model.studentattempt.StudentAttemptService;
import com.example.springdemo.model.studentquestion.StudentQuestion;
import com.example.springdemo.model.studentquestion.StudentQuestionService;
import com.example.springdemo.service.AsyncService;
import com.example.springdemo.util.PageableWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.swing.text.html.Option;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:8081", "https://effortless-quiz.herokuapp.com/"})
public class QuizController {
    @Autowired
    private QuizzesService quizzesService;

    @Autowired
    private StagingQuizzesService stagingQuizzesService;

    @Autowired
    private StudentAttemptService studentAttemptService;

    @Autowired
    private StudentQuestionService studentQuestionService;

    @Autowired
    private QuestionInQuizService questionInQuizService;

    @Autowired
    private AsyncService asyncService;

    @Value("${app.stagingStatusId}")
    private int stagingStatusId;

    @GetMapping("/users/{id}/quizzes")
    @RolesAllowed("ROLE_TEACHER")
    public ResponseEntity getTeacherQuizzes(
            PageableWrapper wrapper,
            @RequestParam Optional<String> q,
            @PathVariable Long id
    ) {
        Pageable pageable = wrapper.getPageable();
        Page<Quizzes> pageOfQuizzes = null;
        if (q.isPresent()) {
            pageOfQuizzes = quizzesService.findByQuizCodeAndUsersId("%" + q.get() + "%", pageable, id);
        } else {
            pageOfQuizzes = quizzesService.findByUsersId(id, pageable);
        }
        if (!pageOfQuizzes.isEmpty()) {
            return ResponseEntity.ok(pageOfQuizzes);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/quizzes/{quizId}")
    @RolesAllowed("ROLE_TEACHER")
    public ResponseEntity getQuizById(
            @PathVariable Long userId,
            @PathVariable Integer quizId
    ) {
        Optional<Quizzes> quizFound = quizzesService.findByIdAndUsersId(userId, quizId);
        if (quizFound.isPresent()) {
            return ResponseEntity.ok(quizFound.get());
        }
        return ResponseEntity.noContent().build();
    }

//    @PutMapping("/users/{id}/studentAttempts/{attemptId}/questionInQuizzes/{questionInQuizId}")
//    public ResponseEntity giveAnswerForQuestion(
//            @PathVariable Long attemptId,
//            @PathVariable Long questionInQuizId,
//            @RequestBody GiveAnswerRequest giveAnswerRequest
//    ) {
//
//        StudentQuestion studentQuestion = studentQuestionService.findOne(giveAnswerRequest.getStudentQuestionId());
//        if (studentQuestion != null) {
//            studentQuestion.setChosenAnswerId(giveAnswerRequest.getChosenAnswerId());
//            studentQuestionService.save(studentQuestion);
//            return ResponseEntity.ok("Successfully saved student answer!!");
//        }
//
////        StudentAttempt studentAttempt = studentAttemptService.findOne(attemptId);
////        QuestionInQuiz questionInQuiz = questionInQuizService.findOne(questionInQuizId);
////        if (studentAttempt != null && questionInQuiz != null) {
////            studentQuestion = new StudentQuestion(studentAttempt, questionInQuiz);
////            studentQuestion.setChosenAnswerId(giveAnswerRequest.getChosenAnswerId());
////            studentQuestionService.save(studentQuestion);
////            return ResponseEntity.ok("Successfully saved student question and answer!!");
////        }
//
//        return ResponseEntity.badRequest().body("Invalid request for saving student question and answer!!");
//    }

    @PutMapping("/users/{id}/studentAttempt/{attemptId}")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity submitStudentAttempt(@PathVariable Integer userId, @PathVariable Long attemptId){
        StudentAttempt studentAttempt = studentAttemptService.findOne(attemptId);
        if(studentAttempt != null){
            studentAttempt.setStatusId(2);
            studentAttemptService.save(studentAttempt);
            return ResponseEntity.ok("Successfully submitted student attempt!");
        }
        return ResponseEntity.badRequest().body("Attempt does not exist!");
    }

    @PutMapping("/users/{id}/studentAttempt/{attemptId}/studentQuestions/{studentQuestionId}")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity giveStudentQuestionAnswer(
            @PathVariable Long attemptId,
            @PathVariable Long studentQuestionId,
            @RequestBody GiveAnswerRequest giveAnswerRequest
    ) {
        StudentQuestion studentQuestion = studentQuestionService.findOne(studentQuestionId);
        if (studentQuestion != null
                && studentQuestion.getStagingQuizzesStatusId() == 4
                && studentQuestion.getStudentAttemptStatusId() == 1
        ) {
            double timeOver = (double) System.currentTimeMillis() - studentQuestion.getStartTime().getTime();
            double remaining = studentQuestion.getDuration() * 60 * 1000 - timeOver;
            if (remaining + 5 * 1000 > 0) {
                studentQuestion.setChosenAnswerId(giveAnswerRequest.getChosenAnswerId());
                studentQuestionService.save(studentQuestion);
                return ResponseEntity.ok("Successfully saved student answer!");
            }
        }
        return ResponseEntity.badRequest().body("Invalid give answer attempt!!");
    }

    @GetMapping("/users/{id}/studentAttempts/{attemptId}")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity getAttemptsQuestions(@PathVariable Long id, @PathVariable Long attemptId) {
        StudentAttempt studentAttempt = studentAttemptService.findOne(attemptId);
        double timeOver = (double) System.currentTimeMillis() - studentAttempt.getStartTime().getTime();

        if (studentAttempt != null) {
            double remaining = studentAttempt.getDuration() * 60 * 1000 - timeOver;
            boolean isNotExpired = remaining + 5 * 1000 > 0;

            if (studentAttempt.getStatusId() == 1) {
                for (StudentQuestion studentQuestion : studentAttempt.getStudentQuestionList()) {
                    for (Answer answer : studentQuestion.getQuestionInQuiz().getQuestion().getAnswerList()) {
                        answer.setCorrect(null);
                    }
                }
            }

            double remainingTime = Math.ceil(remaining / 1000);
            studentAttempt.setRemainingTime((int) remainingTime);
            return ResponseEntity.ok(studentAttempt);
        }

        return ResponseEntity.badRequest().body("Attempt does not exist!!");
    }

    @PutMapping("/users/{userId}/take-quiz")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity takeQuiz(@RequestBody TakeQuizRequest takeQuizRequest, @PathVariable Integer userId) {
        StudentAttempt studentAttemptFound = studentAttemptService.findByUsersIdAndStagingQuizzesId(takeQuizRequest.getStagingQuizzesId(), userId);
        if (studentAttemptFound != null) {
            return ResponseEntity.badRequest().body("Already taken this quiz!!");
        }
        StagingQuizzes launchQuiz = stagingQuizzesService.findByIdAndPassword(
                takeQuizRequest.getStagingQuizzesId(),
                takeQuizRequest.getQuizPassword()
        );
        if (launchQuiz != null) {
            StudentAttempt studentAttempt = new StudentAttempt(userId, launchQuiz);
            for (QuestionInQuiz questionInQuiz : launchQuiz.getQuestionInQuizList()) {
                studentAttempt.getStudentQuestionList().add(new StudentQuestion(studentAttempt, questionInQuiz));
            }
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            studentAttempt.setStartTime(startTime);
            studentAttemptService.save(studentAttempt);
            studentAttempt.setRemainingTime(launchQuiz.getDuration() * 60);
            return ResponseEntity.ok(studentAttempt);
        }
        return ResponseEntity.badRequest().body("Incorrect quiz password!!");
    }

    @GetMapping("/users/{userId}/quizzes/{quizId}/questions")
    public ResponseEntity getQuestionsByQuizId(
            @PathVariable Long userId,
            @PathVariable Integer quizId
    ) {
        StagingQuizzes stagingQuizzes = stagingQuizzesService.findByQuizesIdAndStatusId(quizId, 3).orElse(null);
        if (stagingQuizzes != null) {
            return ResponseEntity.ok(stagingQuizzes.getQuestionInQuizList());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("users/{id}/quizzes")
    @RolesAllowed("ROLE_TEACHER")
    public ResponseEntity addTeacherQuiz(
            @PathVariable Long id,
            @RequestBody Quizzes quiz
    ) {
        quiz.setUsersId(id);
        quiz.setImportDate(new Timestamp(System.currentTimeMillis()));
//        try {
        StagingQuizzes stagingQuizzes = new StagingQuizzes();
        stagingQuizzes.setStatusId(3);
        stagingQuizzes.setQuiz(quiz);
        List<StagingQuizzes> stagingQuizzesList = new ArrayList<>();
        stagingQuizzesList.add(stagingQuizzes);
        quiz.setStagingQuizzesList(stagingQuizzesList);
        quizzesService.save(quiz);
//        } catch (Exception e) {
//
//            if (e.getMessage().contains("")) {
//                return ResponseEntity.badRequest().body(new BadRequestResponse("Quiz Code already exists!!"));
//            }
//        }
        return new ResponseEntity(quiz, HttpStatus.CREATED);
    }

    @PutMapping("users/{id}/quizzes/{quizId}")
    @RolesAllowed("ROLE_TEACHER")
    public ResponseEntity launchQuiz(
            @RequestBody StagingQuizzes launchQuiz,
            @PathVariable Integer quizId
    ) {
        Quizzes quiz = quizzesService.findById(quizId).orElse(null);
        StagingQuizzes stagingQuizzes = quiz.getStagingQuizzesList().stream()
                .filter(stQuiz -> stQuiz.getStatusId() == 3)
                .findAny().orElse(null);

        if (stagingQuizzes.getQuestionInQuizList().isEmpty())
            return ResponseEntity.badRequest().body("Quiz has no questions!!");

        launchQuiz.setQuiz(quiz);
        launchQuiz.setStatusId(4);
        launchQuiz.setImportDate(new Timestamp(System.currentTimeMillis()));
        List<QuestionInQuiz> questionInQuizList = new ArrayList<>();
        for (QuestionInQuiz questionInQuiz : stagingQuizzes.getQuestionInQuizList()) {
            questionInQuizList.add(new QuestionInQuiz(launchQuiz, questionInQuiz.getQuestion()));
        }
        launchQuiz.setQuestionInQuizList(questionInQuizList);
        stagingQuizzesService.save(launchQuiz);
        asyncService.updateLaunchQuizStatus(launchQuiz.getId());
        return new ResponseEntity(launchQuiz, HttpStatus.CREATED);
    }

    @RolesAllowed("ROLE_TEACHER")
    @GetMapping("users/{userId}/quizzes/{quizId}/stagingQuizzes")
    public ResponseEntity getLaunchQuiz(
            PageableWrapper wrapper,
            @PathVariable Integer quizId
    ) {
        Pageable pageable = wrapper.getPageable();
        Page<StagingQuizzes> stagingQuizzesPage = stagingQuizzesService.findByQuizId(quizId, pageable);

        stagingQuizzesPage.forEach(quiz -> {
            quiz.setQuestionInQuizList(null);
        });

        if (stagingQuizzesPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stagingQuizzesPage);
    }

    @RolesAllowed("ROLE_STUDENT")
    @GetMapping("users/{userId}/stagingQuizzes")
    public ResponseEntity getLaunchingQuizByQuizCode(
            PageableWrapper wrapper,
            @RequestParam String quizCode
    ) {
        Pageable pageable = wrapper.getPageable();
        Page<StagingQuizzes> stagingQuizzesPage = stagingQuizzesService.findByQuizCodeAndStatusId(quizCode, pageable);

        stagingQuizzesPage.forEach(quiz -> {
            quiz.setQuizPassword(null);
            quiz.setQuestionInQuizList(null);
        });

        if (stagingQuizzesPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stagingQuizzesPage);
    }

}
