package com.unicauca.FrontManagement.factories;

import com.unicauca.FrontManagement.entity.*;

import java.util.List;

public class HeadOfDepartamentFactory extends PersonFactory {
    @Override
    public Person createPerson(String name, String lastName, String email, String password,
                               String phone, EnumProgram program, String extraAttribute) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(List.of(new Role(EnumRole.JefeDeDepartamento)));
        return HeadOfDepartment.builder()
                .name(name)
                .lastName(lastName)
                .phoneNumber(phone)
                .program(program)
                .user(user)
                .suputamadre(extraAttribute)
                .build();
    }
}


