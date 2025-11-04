package com.unicauca.FrontManagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DegreeProjectEvent {

    private Long Id;

    private String title;

    private String generalObjective;

    private List<String> specificObjectives;

    private String modality;

    private List<FileEvent> files = new ArrayList<FileEvent>();

    private List<Long> studentsId = new ArrayList<Long>();

    private Long directorId;

    private List<Long> codirectorsId = new ArrayList<Long>();

    private StateRequest state;

}
