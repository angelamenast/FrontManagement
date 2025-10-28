package com.unicauca.frontmanagement.services;

import com.unicauca.frontmanagement.entity.Professor;
import com.unicauca.frontmanagement.repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> listProfessors() throws Exception{
        return this.professorRepository.listProfessors();
    }
}
