package com.example.springdemo.model.role;

import com.example.springdemo.model.userrole.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data @AllArgsConstructor @NoArgsConstructor
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

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role(String name) {
        this.name = name;
    }
}