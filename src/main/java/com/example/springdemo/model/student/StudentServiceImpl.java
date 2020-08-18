package com.example.springdemo.model.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Page<Student> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    @Override
    public Page<Student> search(String searchTerm, Pageable pageable) {
        return studentRepository.findByNameContaining(searchTerm, pageable);
    }

    @Override
    @Transactional
    public Student findOne(Integer id) {
        return studentRepository.findById(id)
                .orElse(null);
    }

    @Override
    public void save(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void delete(Integer id) {
        studentRepository.deleteById(id);
    }
}
