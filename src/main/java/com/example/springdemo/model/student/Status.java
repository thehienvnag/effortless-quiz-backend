package com.example.springdemo.model.student;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "status")
@Data
@Entity
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    
}