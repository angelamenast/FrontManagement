package com.unicauca.FrontManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class DegreeProjectRequest {

    private String title;

    private String generalObjective;

    private List<String> specificObjectives;

    private String modality;

    private List<FileRequest> files = new ArrayList<FileRequest>();

    private List<Long> studentsId = new ArrayList<Long>();

    private Long directorId;

    private List<Long> codirectorsId = new ArrayList<Long>();

    private StateRequest state;

}
