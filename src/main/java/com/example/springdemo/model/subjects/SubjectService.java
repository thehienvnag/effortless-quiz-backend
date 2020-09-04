package com.example.springdemo.model.subjects;



import java.util.List;

public interface SubjectService {
    public List<Subjects> findByNameContaining(String term1);
}
