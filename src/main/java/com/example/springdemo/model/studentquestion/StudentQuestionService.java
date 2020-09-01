package com.example.springdemo.model.studentquestion;


public interface StudentQuestionService {
    public void save(StudentQuestion studentQuestion);
    public StudentQuestion findOne(Long id);
    public boolean giveStudentQuestionAnswer(String chosenAnswerId, Long studentQuestionId);

}
