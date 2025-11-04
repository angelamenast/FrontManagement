package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.Person;
import com.unicauca.FrontManagement.entity.Professor;

import java.util.List;

public interface IProfessorRepository {

    public boolean registerProfessor(Person person) throws Exception;
    List<Professor> listProfessors() throws Exception;
}
