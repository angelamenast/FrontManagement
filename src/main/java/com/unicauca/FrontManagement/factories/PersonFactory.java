package com.unicauca.FrontManagement.factories;

import com.unicauca.FrontManagement.entity.EnumProgram;
import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.User;

public abstract class PersonFactory {
    public abstract Person createPerson(String name, String lastName, String email, String password,
                               String phone, EnumProgram program, String extraAttribute);
}

