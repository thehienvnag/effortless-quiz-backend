package com.example.springdemo.controllers;

import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.quizes.QuizzesService;
import com.example.springdemo.model.subjects.SubjectService;
import com.example.springdemo.model.subjects.Subjects;
import com.example.springdemo.util.PageableWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.security.auth.Subject;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8081")
public class SubjectController {
    @Autowired
    private SubjectService subjectService;

    @GetMapping("/subjects")
    public ResponseEntity searchSubjects(@RequestParam Optional<String> q){
        if(q.isPresent() || !q.get().trim().isEmpty()){
            List<Subjects> subjectSearched = subjectService.findByNameContaining("%" + q.get() + "%");
            if(subjectSearched.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(subjectSearched);
        }
        return ResponseEntity.badRequest().body("Required search keyword for subjects finding!!");
    }



}
