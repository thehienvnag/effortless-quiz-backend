package com.example.springdemo.model.userrole;

import com.example.springdemo.model.role.Role;
import com.example.springdemo.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_role")
@Entity
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", insertable = false, nullable = false)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", referencedColumnName = "Id")
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleid", referencedColumnName = "Id")
    private Role role;

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

}