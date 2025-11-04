package com.unicauca.FrontManagement.factories;

import com.unicauca.FrontManagement.entity.*;

import java.util.List;

public class StudentFactory extends PersonFactory {
    @Override
    public Person createPerson(String name, String lastName, String email, String password,
                               String phone, EnumProgram program, String studentCode) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(List.of(new Role(EnumRole.Estudiante)));
        return Student.builder()
                .name(name)
                .lastName(lastName)
                .phoneNumber(phone)
                .program(program)
                .user(user)
                .studentCode(studentCode)
                .build();
    }
}

