package com.example.springdemo.model.studentquestion;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentQuestionRepository extends PagingAndSortingRepository<StudentQuestion, Long> {

    @Query("UPDATE StudentQuestion sq SET sq.chosenAnswerId = ?1 WHERE sq.id = ?2")
    public Integer giveStudentQuestionAnswer(String chosenAnswerId, Long studentQuestionId);


}
