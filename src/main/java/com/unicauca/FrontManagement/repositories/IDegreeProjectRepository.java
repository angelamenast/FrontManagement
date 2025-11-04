package com.unicauca.FrontManagement.repositories;

import com.unicauca.FrontManagement.entity.DegreeProject;
import com.unicauca.FrontManagement.entity.File;
import com.unicauca.FrontManagement.infra.dto.DegreeProjectRequest;

import java.util.List;

public interface IDegreeProjectRepository {
       public int findLastFileVersionByStudentEmail(String email, String fileType) throws Exception;
       public boolean saveProject(DegreeProject degreeProject) throws Exception;
       public boolean reuploadProject(DegreeProject degreeProject) throws Exception;
       public List<DegreeProject> findProjectsByTypeFile(String fileType) throws Exception;
       public void updateProjectAndChanceState(DegreeProject degreeProject, String state) throws Exception;
       public List<DegreeProject> findProjectsWithApprovedFormatA() throws Exception;
       public void savePreliminaryProject(Long projectId, File file)throws Exception;
       public DegreeProject findProjectByEmail(String email) throws Exception;
}
