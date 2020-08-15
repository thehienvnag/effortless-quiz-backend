package com.example.springdemo.model.student;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StudentRepository extends PagingAndSortingRepository<Student, Integer> {

    public Page<Student> findByNameContaining(String term, Pageable pageable);

}
