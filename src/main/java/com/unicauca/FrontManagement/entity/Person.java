package com.unicauca.FrontManagement.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@NoArgsConstructor
@SuperBuilder
public class Person {

    @Getter @Setter
    private Long id;
    @Getter @Setter
    protected String name;
    @Getter @Setter
    protected String lastName;
    @Getter @Setter
    protected String phoneNumber;
    @Getter @Setter
    protected EnumProgram program;
    @Getter @Setter
    protected User user;

}
