package com.example.springdemo.model.user;

import com.example.springdemo.model.quizes.Quizes;
import com.example.springdemo.model.role.Role;
import com.example.springdemo.model.student.Student;
import com.example.springdemo.model.userrole.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "username", nullable = true)
    private String username;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Quizes> quizList;

    @Column(name = "facebookId", nullable = true)
    private String facebookId;

    @Column(name = "refreshToken", nullable = true)
    private String refreshToken;


    public User(String username, String password, String name, String roleName) {
        this.username = username;
        this.password = password;
        this.name = name;
        userRoles = new ArrayList<>();
        userRoles.add(new UserRole(this, new Role(2, roleName)));
    }
}