package com.example.springdemo.model.subjects;

import com.example.springdemo.model.student.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public List<Subjects> findByNameContaining(String term1) {
        return subjectRepository.findByNameOrCodeContaining(term1.trim().toLowerCase());
    }
}
