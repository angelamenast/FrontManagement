package com.unicauca.frontmanagement.repositories;

import com.unicauca.frontmanagement.entity.Person;
import com.unicauca.frontmanagement.entity.Student;

public interface IStudentRepository {

    public boolean registerStudent(Person person) throws Exception;

}
