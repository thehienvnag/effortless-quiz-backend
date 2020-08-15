package com.example.springdemo.security;

import com.example.springdemo.model.student.Student;
import com.example.springdemo.model.student.StudentServiceImpl;
import com.example.springdemo.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {


    @Autowired
    private StudentServiceImpl studentService;

    @Override
    public boolean hasPermission(
            Authentication auth,
            Object targetDomainObject,
            Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication auth,
            Serializable targetId,
            String targetType,
            Object permission) {
        if (auth == null && targetId == null && targetType == null && permission == null) {
            return false;
        }
        boolean checkRole = auth.getAuthorities().stream().anyMatch(
                (grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    return authority.contains((String) permission);
                })
        );
        return checkRole && hasPrivilege(auth, (Integer) targetId);
    }

    private boolean hasPrivilege(Authentication auth, Integer targetId) {
        Student student = studentService.findOne(targetId);
        if (student == null || student.getUser() == null) {
            return false;
        }
        return student.getUser().getUsername().equals(auth.getName());
    }

}
