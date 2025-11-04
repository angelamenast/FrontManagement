package com.unicauca.FrontManagement.services;

import com.unicauca.FrontManagement.entity.DegreeProject;
import com.unicauca.FrontManagement.entity.File;
import com.unicauca.FrontManagement.repositories.DegreeProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DegreeProjectService {

    @Autowired
    private DegreeProjectRepository degreeProjectRepository;

    public int findLastFileVersionByStudentEmail(String email, String type) throws Exception{
        return degreeProjectRepository.findLastFileVersionByStudentEmail(email,type);
    }

    public boolean saveProject(DegreeProject degreeProject) throws Exception {
        return degreeProjectRepository.saveProject(degreeProject);
    }

    public boolean reuploadProject(DegreeProject degreeProject) throws Exception {
        return degreeProjectRepository.reuploadProject(degreeProject);
    }

    public void updateProjectAndChanceState(DegreeProject degreeProject, String state) throws Exception{
        degreeProjectRepository.updateProjectAndChanceState(degreeProject,state);
    }

    public List<DegreeProject> findProjectsWithApprovedFormatA() throws Exception{
        return degreeProjectRepository.findProjectsWithApprovedFormatA();
    }

    public void savePreliminaryProject(Long projectId, File file ) throws Exception{
        degreeProjectRepository.savePreliminaryProject(projectId, file);
    }

    public List<DegreeProject> findProjectsByTypeFile(String fileType) throws Exception{
        return degreeProjectRepository.findProjectsByTypeFile(fileType);
    }

    public DegreeProject findProjectByEmail(String email) throws Exception{
        return degreeProjectRepository.findProjectByEmail(email);
    }

}

