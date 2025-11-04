package com.unicauca.FrontManagement.services;

import com.unicauca.FrontManagement.entity.Professor;
import com.unicauca.FrontManagement.repositories.ProfessorRepository;
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
