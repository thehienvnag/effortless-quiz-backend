package com.example.springdemo.controllers;

import com.example.springdemo.model.quizes.QuizesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8081")
public class QuizController {
    @Autowired
    private QuizesService quizesService;


}
