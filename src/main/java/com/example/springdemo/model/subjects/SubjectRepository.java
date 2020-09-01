package com.example.springdemo.model.subjects;


import com.example.springdemo.model.student.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SubjectRepository extends CrudRepository<Subjects, Integer> {

    @Query("SELECT s FROM Subjects s WHERE lower(name) LIKE ?1 OR lower(code) LIKE ?1")
    public List<Subjects> findByNameOrCodeContaining(String term1);

}
