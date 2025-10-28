package com.unicauca.frontmanagement.infra.dto;

import lombok.Getter;
import lombok.Setter;

public class ProfessorRequest {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String phoneNumber;
    @Getter @Setter
    private String program;
    @Getter @Setter
    private UserRequest userRequest;
    @Getter @Setter
    private String office;

}
