package com.unicauca.frontmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Getter @Setter
    private String email;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private List<Role> roles;

}
