package com.example.springdemo.controllers;

import com.example.springdemo.model.answer.Answer;
import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import com.example.springdemo.model.questioninquiz.QuestionInQuizService;
import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.quizes.QuizzesService;
import com.example.springdemo.model.requests.GiveAnswerRequest;
import com.example.springdemo.model.requests.TakeQuizRequest;
import com.example.springdemo.model.responses.TakeQuizResponse;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.stagingquizzes.StagingQuizzesService;
import com.example.springdemo.model.studentattempt.StudentAttempt;
import com.example.springdemo.model.studentattempt.StudentAttemptService;
import com.example.springdemo.model.studentquestion.StudentQuestion;
import com.example.springdemo.model.studentquestion.StudentQuestionService;
import com.example.springdemo.security.UserPrincipal;
import com.example.springdemo.service.AsyncService;
import com.example.springdemo.util.PageableWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:8081")
@CrossOrigin(origins = "https://effortless-quiz.herokuapp.com")
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
            @PathVariable Long id,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (id != userPrincipal.getId().longValue()) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }
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
            @PathVariable Integer quizId,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer authUserId = userPrincipal.getId();
        if (userId != authUserId.longValue()) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }

        Quizzes quizFound = quizzesService.findByIdAndUsersId(userId, quizId).orElse(null);
        if (quizFound != null) {

            return ResponseEntity.ok(quizFound);
        }
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/users/{userId}/studentAttempt/{attemptId}")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity submitStudentAttempt(
            @PathVariable Integer userId,
            @PathVariable Long attemptId,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Integer authUserId = userPrincipal.getId();
        if (userId != authUserId) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }

        StudentAttempt studentAttempt = studentAttemptService.findByUserIdAndAttemptId(attemptId, userId);
        if (studentAttempt != null) {
            if (studentAttempt.getStatusId() == 1 && studentAttempt.isNotExpired()) {
                double countPoints = 0;
                for (StudentQuestion studentQuestion : studentAttempt.getStudentQuestionList()) {
                    int countCorrect = studentQuestion.getQuestionInQuiz().getQuestion().getCountCorrectAnswer();
                    int countStudentCorrect = 0;
                    for (Answer answer : studentQuestion.getQuestionInQuiz().getQuestion().getAnswerList()) {
                        if (studentQuestion.getChosenAnswerId() != null
                                && studentQuestion.getChosenAnswerId().contains(answer.getId().toString())
                        ) {
                            if (answer.getCorrect()) {
                                countStudentCorrect++;
                            } else {
                                countStudentCorrect--;
                            }
                        }
                    }
                    double questionPoint = 0;
                    if (countStudentCorrect > 0) {
                        questionPoint = (double) countStudentCorrect / countCorrect;
                    }
                    studentQuestion.setPointEarned(questionPoint);
                    studentQuestionService.save(studentQuestion);
                    countPoints += questionPoint;
                }
                studentAttempt.setStatusId(2);
                studentAttempt.setPointsEarned(countPoints);
                double grade = (countPoints / studentAttempt.getQuestionCount()) * 10;
                studentAttempt.setQuizGrade(grade);
                studentAttemptService.save(studentAttempt);
                studentAttempt.setStudentQuestionList(null);
                return ResponseEntity.ok(studentAttempt);
            }

        }
        return ResponseEntity.badRequest().body("Attempt does not exist!");
    }

    @PutMapping("/users/{id}/studentAttempt/{attemptId}/studentQuestions/{studentQuestionId}")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity giveStudentQuestionAnswer(
            @PathVariable Long attemptId,
            @PathVariable Long studentQuestionId,
            @RequestBody GiveAnswerRequest giveAnswerRequest,
            Authentication authentication
    ) {

        StudentQuestion studentQuestion = studentQuestionService.findOne(studentQuestionId);
        if (studentQuestion != null) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            if (studentQuestion.getUserId() != userPrincipal.getId()) {
                return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
            }
            if (studentQuestion.getStagingQuizzesStatusId() == 4 && studentQuestion.getStudentAttemptStatusId() == 1) {
                double timeOver = (double) System.currentTimeMillis() - studentQuestion.getStartTime().getTime();
                double remaining = studentQuestion.getDuration() * 60 * 1000 - timeOver;
                if (remaining + 5 * 1000 > 0) {
                    studentQuestion.setChosenAnswerId(giveAnswerRequest.getChosenAnswerId());
                    studentQuestionService.save(studentQuestion);
                    return ResponseEntity.ok("Successfully saved student answer!");
                }
            }

        }
        return ResponseEntity.badRequest().body("Invalid give answer attempt!!");
    }

    @GetMapping("/users/{id}/studentAttempts")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity getStudentAttempts(
            @PathVariable Long id,
            PageableWrapper pageableWrapper,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (id != userPrincipal.getId().longValue()) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }
        Pageable pageable = pageableWrapper.getPageable();
        Page<StudentAttempt> studentAttempt = studentAttemptService.findByUsersId(id.intValue(), pageable);
        if (studentAttempt.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        studentAttempt.stream().forEach(stuAttempt -> {
            stuAttempt.setStudentQuestionList(null);
        });
        return ResponseEntity.ok(studentAttempt);
    }

    @GetMapping("/users/{id}/studentAttempts/{attemptId}")
    @RolesAllowed("ROLE_STUDENT")
    public ResponseEntity getAttemptsQuestions(
            @PathVariable Long id,
            @PathVariable Long attemptId,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String role = userPrincipal.getAuthorities().iterator().next().toString();
        if (id != userPrincipal.getId().longValue() && !role.equals("ROLE_TEACHER")) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }
        StudentAttempt studentAttempt = studentAttemptService.findOne(attemptId);
        if (studentAttempt != null) {
            if (studentAttempt.getStatusId() == 7) {
                studentAttempt.setStatusId(1);

            }
            if (studentAttempt.getStatusId() == 1) {
                for (StudentQuestion studentQuestion : studentAttempt.getStudentQuestionList()) {
                    for (Answer answer : studentQuestion.getQuestionInQuiz().getQuestion().getAnswerList()) {
                        answer.setCorrect(null);
                    }
                }
            }
            studentAttempt.setStartTime(new Timestamp(System.currentTimeMillis()));
            studentAttemptService.save(studentAttempt);
            return ResponseEntity.ok(studentAttempt);
        }
        return ResponseEntity.badRequest().body("Invalid! Attempt is not allowed!!!");
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
        if (launchQuiz.getQuiz().getStatusId() != 1) {
            return ResponseEntity.badRequest().body("This quiz is disabled!");
        }
        if (launchQuiz != null) {
            StudentAttempt studentAttempt = new StudentAttempt(userId, launchQuiz);
            for (QuestionInQuiz questionInQuiz : launchQuiz.getQuestionInQuizList()) {
                if (questionInQuiz.getStatusId() == 1) {
                    studentAttempt.getStudentQuestionList().add(new StudentQuestion(studentAttempt, questionInQuiz));
                }

            }
            studentAttemptService.save(studentAttempt);
            return ResponseEntity.ok(
                    new TakeQuizResponse(
                            studentAttempt.getId(),
                            "Successfully save student attempt!"
                    )
            );
        }
        return ResponseEntity.badRequest().body("Incorrect quiz password!!");
    }

    @GetMapping("/users/{userId}/quizzes/{quizId}/questions")
    public ResponseEntity getQuestionsByQuizId(
            @PathVariable Integer userId,
            @PathVariable Integer quizId,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userId != userPrincipal.getId()) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }

        StagingQuizzes stagingQuizzes = stagingQuizzesService.findByQuizesIdAndStatusIdAndUserId(quizId, 3, userId).orElse(null);
        if (stagingQuizzes != null) {
            List<QuestionInQuiz> questionInQuizList = stagingQuizzes.getQuestionInQuizList()
                    .stream()
                    .filter(questionInQuiz -> questionInQuiz.getStatusId() == 1)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(questionInQuizList);
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
        try {
            StagingQuizzes stagingQuizzes = new StagingQuizzes();
            stagingQuizzes.setStatusId(3);
            stagingQuizzes.setQuiz(quiz);
            List<StagingQuizzes> stagingQuizzesList = new ArrayList<>();
            stagingQuizzesList.add(stagingQuizzes);
            quiz.setStagingQuizzesList(stagingQuizzesList);
            quiz.setStatusId(1);
            quizzesService.save(quiz);
        } catch (Exception e) {
            if (e.getMessage().contains("")) {
                return ResponseEntity.badRequest().body("Quiz Code already exists!!");
            }
        }
        return new ResponseEntity(quiz, HttpStatus.CREATED);
    }

    @PutMapping("users/{id}/quizzes/{quizId}")
    @RolesAllowed("ROLE_TEACHER")
    public ResponseEntity launchQuiz(
            @RequestBody(required = false) StagingQuizzes launchQuiz,
            @PathVariable Integer quizId,
            @RequestParam(required = false) Boolean disabled,
            Authentication authentication
    ) {
        Quizzes quiz = quizzesService.findById(quizId).orElse(null);
        if (quiz != null) {
            if (disabled != null && disabled) {
                quiz.setStatusId(2);
                quizzesService.save(quiz);
                return ResponseEntity.ok("Successfully save");
            } else {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                if (userPrincipal.getId() != quiz.getUsersId().intValue()) {
                    return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
                }
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
                    if (questionInQuiz.getStatusId() == 1) {
                        questionInQuizList.add(new QuestionInQuiz(launchQuiz, questionInQuiz.getQuestion()));
                    }
                }
                launchQuiz.setQuestionInQuizList(questionInQuizList);
                stagingQuizzesService.save(launchQuiz);
                asyncService.updateLaunchQuizStatus(launchQuiz.getId());
                return new ResponseEntity(launchQuiz, HttpStatus.CREATED);
            }

        }
        return ResponseEntity.badRequest().body("Quiz does not exist!");
    }


    @RolesAllowed("ROLE_TEACHER")
    @GetMapping("users/{userId}/quizzes/{quizId}/stagingQuizzes")
    public ResponseEntity getLaunchQuiz(
            PageableWrapper wrapper,
            @PathVariable Integer quizId,
            @PathVariable Integer userId,
            Authentication authentication
    ) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal.getId() != userId) {
            return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
        }

        Pageable pageable = wrapper.getPageable();
        Page<StagingQuizzes> stagingQuizzesPage = stagingQuizzesService.findByQuizIdAndUserId(quizId, userId.longValue(), pageable);

        stagingQuizzesPage.forEach(quiz -> {
            quiz.setQuestionInQuizList(null);
        });

        if (stagingQuizzesPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stagingQuizzesPage);
    }

    @RolesAllowed("ROLE_TEACHER")
    @PutMapping("users/{userId}/stagingQuizzes/{stagingQuizzesId}")
    public ResponseEntity changeReviewableStatus(
            @PathVariable Long userId,
            @PathVariable Long stagingQuizzesId,
            @RequestParam(required = false) Boolean cancelable,
            @RequestParam(required = false) Boolean reviewable,
            Authentication authentication
    ) {

        StagingQuizzes stagingQuizzes = stagingQuizzesService.findOne(stagingQuizzesId).orElse(null);
        if (stagingQuizzes != null) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            if (userPrincipal.getId() != stagingQuizzes.getQuiz().getUsersId().intValue()) {
                return new ResponseEntity("You are not authorized to access this resource!", HttpStatus.FORBIDDEN);
            }
            if (reviewable != null && reviewable) {
                Boolean currentReviewableStatus = stagingQuizzes.getReviewable();
                if (reviewable == null) {
                    currentReviewableStatus = true;
                } else {
                    currentReviewableStatus = !currentReviewableStatus;
                }
                stagingQuizzes.setReviewable(currentReviewableStatus);
            }
            if (cancelable != null && cancelable) {
                stagingQuizzes.setStatusId(6);
            } else {
                if (stagingQuizzes.getQuiz().getUsersId() != userId) {
                    return new ResponseEntity("You don't have permission!", HttpStatus.FORBIDDEN);
                }

            }
            stagingQuizzesService.save(stagingQuizzes);
            return ResponseEntity.ok("Successfully saved!");
        }
        return ResponseEntity.badRequest().body("Staging quiz does not exist!");
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

    @RolesAllowed("ROLE_TEACHER")
    @GetMapping("users/{userId}/stagingQuizzes/{stagingQuizzesId}/studentAttempts")
    public ResponseEntity getStudentAttemptOfQuiz(
            @PathVariable Long userId,
            @PathVariable Long stagingQuizzesId,
            Authentication authentication,
            PageableWrapper pageableWrapper
    ){
        Integer userIdAuth = ((UserPrincipal) authentication.getPrincipal()).getId();
        if(userIdAuth != userId.intValue()){
            return new ResponseEntity("You are not allowed to access this resource!", HttpStatus.FORBIDDEN);
        }
        StagingQuizzes stagingQuizzes = stagingQuizzesService.findOne(stagingQuizzesId).orElse(null);
        if(stagingQuizzes != null && stagingQuizzes.getQuiz().getUsersId() == userId) {
            Pageable pageable = pageableWrapper.getPageable();
            Page<StudentAttempt> studentAttemptPage = studentAttemptService.findByStagingQuizzesId(stagingQuizzes.getId(), pageable);
            if(!studentAttemptPage.isEmpty()){
                studentAttemptPage.stream().forEach(studentAttempt -> {studentAttempt.setStudentQuestionList(null);});
                return ResponseEntity.ok(studentAttemptPage);
            }
        }
        return ResponseEntity.noContent().build();
    }

}
