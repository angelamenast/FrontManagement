package com.unicauca.frontmanagement.repositories;

import com.unicauca.frontmanagement.entity.Person;
import com.unicauca.frontmanagement.entity.Professor;

import java.util.List;

public interface IProfessorRepository {

    public boolean registerProfessor(Person person) throws Exception;
    List<Professor> listProfessors() throws Exception;
}
