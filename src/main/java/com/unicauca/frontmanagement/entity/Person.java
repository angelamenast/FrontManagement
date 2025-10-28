package com.unicauca.frontmanagement.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@NoArgsConstructor
@SuperBuilder
public abstract class Person {

    @Getter @Setter
    private Long id;
    @Getter @Setter
    protected String name;
    @Getter @Setter
    protected String lastName;
    @Getter @Setter
    protected String phoneNumber;
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    protected EnumProgram program;
    @Getter @Setter
    protected User user;

}
