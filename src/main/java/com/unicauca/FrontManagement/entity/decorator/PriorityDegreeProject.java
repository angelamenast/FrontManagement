package com.unicauca.FrontManagement.entity.decorator;

import com.unicauca.FrontManagement.entity.DegreeProject;

public class PriorityDegreeProject extends ProjectDecorator {
    public PriorityDegreeProject(DegreeProject project) {
        super(project);
    }

    @Override
    public String getTitle() {
        return project.getTitle() + " [Alta prioridad]";
    }
}