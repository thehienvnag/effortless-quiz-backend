package com.example.springdemo.model.role;

import com.example.springdemo.model.userrole.UserRole;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "role")
@Entity
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "Name", nullable = false)
    private String name;

    @OneToOne(mappedBy = "role", fetch = FetchType.LAZY)
    private UserRole userRole;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
      return "Role{id=" + id + 
        ", name=" + name + 
        "}";
    }
}