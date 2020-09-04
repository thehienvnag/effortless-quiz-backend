package com.example.springdemo.model.userrole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    UserRoleRepository userRoleRepository;


    @Override
    public void removeRole(UserRole userRole) {
        userRoleRepository.delete(userRole);
    }
}
