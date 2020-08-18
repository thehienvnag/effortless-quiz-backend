package com.example.springdemo.model.subjects;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "subjects")
@Entity
public class Subjects implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    
}