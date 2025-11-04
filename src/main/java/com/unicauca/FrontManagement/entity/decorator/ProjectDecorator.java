package com.unicauca.FrontManagement.entity.decorator;

import com.unicauca.FrontManagement.entity.DegreeProject;

public class ProjectDecorator extends DegreeProject {

    DegreeProject project;

    public ProjectDecorator(DegreeProject project) {
        this.project = project;
    }

    public DegreeProject getOriginalProject() {
        return project;
    }


}
