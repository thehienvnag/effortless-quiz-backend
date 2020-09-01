package com.example.springdemo.model.subjects;


import com.example.springdemo.model.student.Student;

import java.util.List;

public interface SubjectService {
    public List<Subjects> findByNameContaining(String term1);
}
