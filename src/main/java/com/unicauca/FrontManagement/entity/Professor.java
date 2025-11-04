package com.unicauca.FrontManagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
@NoArgsConstructor
@SuperBuilder
public class Professor extends Person{
    @Getter @Setter
    private String office;

}
