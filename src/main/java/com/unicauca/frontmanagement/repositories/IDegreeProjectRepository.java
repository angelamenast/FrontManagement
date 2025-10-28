package com.unicauca.frontmanagement.repositories;

import com.unicauca.frontmanagement.entity.DegreeProject;

public interface IDegreeProjectRepository {
       public int findLastFileVersionByStudentEmail(String email, String fileType) throws Exception;
       public boolean saveProject(DegreeProject degreeProject) throws Exception;
}
