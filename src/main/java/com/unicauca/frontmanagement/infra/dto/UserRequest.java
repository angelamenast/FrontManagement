package com.unicauca.frontmanagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class UserRequest {

    @Getter @Setter
    private String email;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private List<String> roles;

}

