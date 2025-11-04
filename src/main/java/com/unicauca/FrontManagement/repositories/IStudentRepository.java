package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.Person;

public interface IStudentRepository {

    public boolean registerStudent(Person person) throws Exception;

}
