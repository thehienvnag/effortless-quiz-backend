package com.example.springdemo.model.studentquestion;

import com.example.springdemo.model.studentattempt.StudentAttempt;
import com.example.springdemo.model.studentattempt.StudentAttemptRepository;
import com.example.springdemo.model.studentattempt.StudentAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentQuestionServiceImpl implements StudentQuestionService {

    @Autowired
    private StudentQuestionRepository studentQuestionRepository;


    @Override
    public void save(StudentQuestion studentQuestion) {
        studentQuestionRepository.save(studentQuestion);
    }

    @Override
    public StudentQuestion findOne(Long id) {
        if(id == null) return null;
        return studentQuestionRepository.findById(id).orElse(null);
    }

    @Override
    public boolean giveStudentQuestionAnswer(String chosenAnswerId, Long studentQuestionId) {
        return this.studentQuestionRepository.giveStudentQuestionAnswer(chosenAnswerId, studentQuestionId) == 1;
    }

}
