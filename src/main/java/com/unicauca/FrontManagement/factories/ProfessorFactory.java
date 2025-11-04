package com.unicauca.FrontManagement.factories;

import com.unicauca.FrontManagement.entity.*;

import java.util.List;

public class ProfessorFactory extends PersonFactory {
    @Override
    public Person createPerson(String name, String lastName, String email, String password,
                               String phone, EnumProgram program, String extraAttribute) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(List.of(new Role(EnumRole.Profesor)));
        return Professor.builder()
                .name(name)
                .lastName(lastName)
                .phoneNumber(phone)
                .program(program)
                .user(user)
                .office(extraAttribute)
                .build();
    }
}


