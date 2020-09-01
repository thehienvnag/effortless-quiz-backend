package com.example.springdemo.model.user;

//import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.quizes.Quizzes;
import com.example.springdemo.model.role.Role;
import com.example.springdemo.model.student.Student;
import com.example.springdemo.model.userrole.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    @Column(name = "username", nullable = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserRole> userRoles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "facebookId", nullable = true)
    private String facebookId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "refreshToken", nullable = true)
    private String refreshToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Quizzes> quizzesList;

    public User(String username, String password, String name, String roleIdStr) {
        this.username = username;
        this.password = password;
        this.name = name;
        userRoles = new ArrayList<>();
        Integer roleId = Integer.parseInt(roleIdStr);

        userRoles.add(new UserRole(this, new Role(roleId)));
    }

    public User(String name, String facebookId) {
        this.name = name;
        this.facebookId = facebookId;
        userRoles = new ArrayList<>();

    }
}