package com.unicauca.frontmanagement.services;

import com.unicauca.frontmanagement.entity.DegreeProject;
import com.unicauca.frontmanagement.repositories.DegreeProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DegreeProjectService {

    @Autowired
    private DegreeProjectRepository degreeProjectRepository;

    public int findLastFileVersionByStudentEmail(String email, String type) throws Exception{
        return degreeProjectRepository.findLastFileVersionByStudentEmail(email,type);
        //te odio macarela :O
    }

    public boolean saveProject(DegreeProject degreeProject) throws Exception {
        return degreeProjectRepository.saveProject(degreeProject);
    }

}

