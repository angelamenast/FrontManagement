package com.unicauca.frontmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class DegreeProject {

    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String title;
    @Getter @Setter
    private String generalObjective;
    @Getter @Setter
    private List<String> specificObjectives;
    @Getter @Setter
    private EnumModality modality;

    @Getter @Setter
    private List<File> files = new ArrayList<File>();

    @Getter @Setter
    private List<Student> students = new ArrayList<Student>();

    @Getter @Setter
    private Professor director;

    @Getter @Setter
    private List<Professor> codirectors = new ArrayList<Professor>();

    @Getter @Setter
    private String state;

}
