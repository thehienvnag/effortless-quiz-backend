package com.example.springdemo.controllers;

import com.example.springdemo.model.answer.Answer;
import com.example.springdemo.model.questioninquiz.QuestionInQuiz;
import com.example.springdemo.model.questioninquiz.QuestionInQuizService;
import com.example.springdemo.model.questions.Question;
import com.example.springdemo.model.questions.QuestionService;
import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.quizes.QuizzesService;
import com.example.springdemo.model.requests.QuestionRequest;
import com.example.springdemo.model.stagingquizzes.StagingQuizzes;
import com.example.springdemo.model.stagingquizzes.StagingQuizzesService;
import com.example.springdemo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:8081", "https://effortless-quiz.herokuapp.com/"})
public class QuestionController {

    @Autowired
    FileService fileService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private StagingQuizzesService stagingQuizzesService;

    @Autowired
    private QuestionInQuizService questionInQuizService;

    @Autowired
    private QuizzesService quizzesService;

    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {

        String imageLink = fileService.uploadFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/pictures/")
                .path(imageLink)
                .toUriString();

        return ResponseEntity.ok(fileDownloadUri);
    }

    @GetMapping("/pictures/{fileName:.+}")
    public ResponseEntity getPicture(@PathVariable String fileName, HttpServletRequest request) {

        Resource resource = fileService.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            return ResponseEntity.notFound().build();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/users/{userId}/quizzes/{id}/questions")
    public ResponseEntity saveQuestions(@PathVariable Integer id, @RequestBody QuestionRequest questionRequest) {

        StagingQuizzes addedStagingQuizzes = stagingQuizzesService.findByQuizesIdAndStatusId(id, 3).orElse(null);

        if (addedStagingQuizzes != null) {
            for (QuestionInQuiz questionInQuiz: addedStagingQuizzes.getQuestionInQuizList()) {
                questionInQuiz.setStagingQuizzes(null);
                questionInQuiz.setQuestion(null);
            }
            addedStagingQuizzes.getQuestionInQuizList().clear();
            for (Question question : questionRequest.getQuestions()) {
                for (Answer answer : question.getAnswerList()) {
                    answer.setQuestion(question);
                }
                addedStagingQuizzes.getQuestionInQuizList().add(new QuestionInQuiz(addedStagingQuizzes, question));
            }
            questionService.saveAll(questionRequest.getQuestions());
            stagingQuizzesService.save(addedStagingQuizzes);
            return ResponseEntity.ok(addedStagingQuizzes.getQuestionInQuizList());
        }
        return ResponseEntity.badRequest().body("Quiz does not exist!");
    }
}
