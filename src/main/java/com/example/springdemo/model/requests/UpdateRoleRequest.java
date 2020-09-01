package com.example.springdemo.model.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Setter
@Getter
public class UpdateRoleRequest {
    @NotBlank
    private String roleId;
}
